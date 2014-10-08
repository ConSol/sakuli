# Gearman Receiver
This page describes how Sakuli can be configured to transmit its test results directly into the *Gearman queue* of the monitoring system. 
![sakuli-db-receiver](../pics/sakuli-gearman.png)

FIXME Nagios service check. The configured `testsuite.id` in the `testsuite.properties` file, will be used as __service name__.
The results (ok and not ok results) will be available in the __"Status Information"__ of the service check.  

## OMD Configuration

### Enable the site's mod-gearman server

Stop the OMD site:

	OMD[sakuli]:~$ omd stop

Start the OMD configuration menu

	OMD[sakuli]:~$ omd config
	
Select *Distributed Monitoring* and

* *-> MOD_GEARMAN -> ON* 
* *-> GEARMAND_PORT -> [IP of OMD]:[4730 / or choose any other free port]* 
* *-> GEARMAN_WORKER -> OFF*

Unless you make already use of mod-gearman (and *only* then), the following settings should be set in *~/etc/mod-gearman/server.cfg*: 

	eventhandler=no
	services=no
	hosts=no
	do_hostchecks=no
	encryption=yes FIXME
	
FIXME encryption

	  
#### Nagios service

FIXME Create a check_command, which will be executed only if Nagios did not receive a Sakuli result within the last 30 minutes. This ensures that you get a notification even if no passive check results arrive in Nagios at all:   

	vim ~/etc/nagios/conf.d/commands.cfg
	
	define command {
	  command_name                   check_dummy
	  command_line                   $USER1$/check_dummy $ARG1$ $ARG2$
	}


Create a host object for the host on which Sakuli checks will be executed: 

	vim ~/etc/nagios/conf.d/hosts.cfg
	
	define host {
	  host_name                      win7sakuli
	  alias                          Windows 7 Sakuli
	  address                        [IP]
	  use                            generic-host
	}

Create the following service object for the first test case: 

	vim ~/etc/nagios/conf.d/services.cfg
	
	define service {
	  service_description            sakuli_demo
	  host_name                      win7sakuli
	  use                            generic-service,srv-pnp
	  active_checks_enabled          0
	  check_command                  check_dummy!3!'Did not receive any Sakuli result within 30 minutes.'
	  check_freshness                1
	  freshness_threshold            1800
	  passive_checks_enabled         1
	}
	
Reload OMD:

	omd reload
	
Now open Thruk; you should see now the Sakuli host with one service attached: 

![omd_pending2](../pics/omd-pending2.png)

The check is waiting now for check results from Sakuli clients. 





## Sakuli Configuration
Open `%SAKULI_HOME\_include\sakuli.properties` on the Sakuli client: 

### Gearman parameters

	sakuli.receiver.gearman.enabled=true
	sakuli.receiver.gearman.server.host=[IPofOMD]
	sakuli.receiver.gearman.server.port=[Gearman Port defined in "omd config"]
	sakuli.receiver.gearman.server.queue=check_results
	
	# Nagios check settings
	# default nagios host_name (can be overwritten in testsuite.properties) 
	sakuli.receiver.gearman.nagios.hostname=win7sakuli
	# gets appended to the performance data in order to set the name of PNP4nagios template
	sakuli.receiver.gearman.nagios.check_command=check_sakuli
	
### (Optional) Customize nagios output message templates 

 Check results submitted via gearman are built upon the following macros: 

	suite.summary 							-> status summary of the test suite
	<HTML table> 
	 |- suite.table 						-> status summary (short) of the test suite
	 |- case.(ok|warning|critical|error) 	-> status summary of case 1
	 |- case.(ok|warning|critical|error) 	-> status summary of case 2
	 |- case.(ok|warning|critical|error) 	-> status summary of case n
	</HTML table>

Each of the six macros above is set by default to the following: 

	#sakuli.receiver.gearman.nagios.output.suite.summary= {{state}} - {{state_short}} Sakuli suite "{{id}}" ran in {{duration}} seconds - {{suite_summary}}. (Last suite run: {{stop_date}})
	#sakuli.receiver.gearman.nagios.output.suite.table={{state_short}} Sakuli suite "{{id}}" ran in {{duration}} seconds - {{suite_summary}}. (Last suite run: {{stop_date}})
	#sakuli.receiver.gearman.nagios.output.case.ok={{state_short}} case "{{id}}" ran in {{duration}}s - {{state_description}}
	#sakuli.receiver.gearman.nagios.output.case.warning={{state_short}} case "{{id}}" over runtime ({{duration}}s /{{state_description}} at {{warning_threshold}}s) {{step_information}}
	#sakuli.receiver.gearman.nagios.output.case.critical={{state_short}} case "{{id}}" over runtime ({{duration}}s /{{state_description}} at {{critical_threshold}}s) {{step_information}}
	#sakuli.receiver.gearman.nagios.output.case.error={{state_short}} case "{{id}}" {{state_description}}: {{error_message}}

 
To change the text of one or more macro, you can use variables in the format `{{variable_name}}`. In this way, Sakuli notifications can be formatted as flexible as possible. Notice that not every variable gets filled in every situation; if not, they are empty. The following variables are available:

* __scope based placeholders__  -  will be filled with the information from the test suite or from the case in dependency of the message type:
    * `{{state}}` state in uppercase like __OK__ or __WARNING__   
    * `{{state_short}}` short marker for the state like __[OK]__ or __[WARN]__  
    * `{{state_description}}` short description of the state like __ok__ or __warning in step__
    * `{{id}}` identifier of the suite or the case 
    * `{{NAME}}` name of the suite or the case 
    * `{{duration}}` duration in seconds  
    * `{{start_date}}` date when execution started, formated as string `dd.MM HH:mm:ss` 
    * `{{stop_date}}` date when execution stopped, formated as string `dd.MM HH:mm:ss` 
    * `{{error_message}}` exception message log 
    * `{{error_screenshot}}` screenshot of the screen when the exception occurs 
    * `{{warning_threshold}}` warning threshold of the runtime in seconds 
    * `{{critical_threshold}}` critical threshold of the runtime in seconds 
* __suite specific placeholders__  -  will be only filled by `sakuli.receiver.gearman.nagios.output.suite.XXX` messages:
    * `{{suite_summary}}` short summary with all important information of the current test suite execution 
    * `{{suite_folder}}` path to the used test suite folder 
    * `{{browser_info}}` detailed information about the used browser
    * `{{host}}` host name where the test has been executed
* __case specific placeholders__  -  will be only filled by `sakuli.receiver.gearman.nagios.output.case.XXX` messages:
    * `{{case_file}}` path and filename of the test case file like `path-of-suite/test-case/_tc.js` 
    * `{{case_start_URL}}` URL of the first visited website at the case  
    * `{{case_last_URL}}` URL of the last visited website at the case 
    * `{{step_information}}` information about the executed steps with warnings (steps inside of there warning threshold, won't be added) 

