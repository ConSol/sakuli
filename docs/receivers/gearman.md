# Configuration of the Gearman Receiver
This Receiver will be used for an __direct communication__ with an gearman daemon aware Nagios installation.
The technique behind this is to sent the results of a finished Sakuli test packed as an gearman message to the configured
gearman queue. Afterwards the nagios server will interpret the message and add the results to the corresponding
Nagios service check. The configured `testsuite.id` in the `testsuite.properties` file, will be used as __service name__.
The results (OK or NOT OK results) will be available in the __"Status Information"__ of the service check.  

To enable the gearman receiver on the _Sakuli client_ and on the _Nagios server_ side
please follow the bellow configuration guide.
 
## Configure the `sakuli.properties` file
### Enable the gearman receiver and configure the gearman connection
 
  ```
  ##### GEARMAN - RECEIVER
  # DEFAULT: false
  sakuli.receiver.gearman.enabled=true
  ## gearman connection settings
  sakuli.receiver.gearman.server.host=gearman-server-host
  sakuli.receiver.gearman.server.port=4730
  sakuli.receiver.gearman.server.queue=check_results
  
  ```

### Set the nagios __host name__ and the __check_command__ to add the results to corresponding nagios environment

  ```
  ## settings for the nagios check
  #sakuli.receiver.gearman.nagios.hostname=sakuli-test-client
  #sakuli.receiver.gearman.nagios.check_command=check_sakuli_db_suite
   
  ```

### (Optional) Customize your nagios output message templates 
The result of a Sakuli test will be printed out in the __"Status Information"__ of the corresponding service check as a
small overview HTML table with the most important information in it. Therefore you configure the bellow mentioned properties:
 
  ```
  # message.type=output-message-template
  sakuli.receiver.gearman.nagios.output.suite.summary= {{state}} - {{state_short}} Sakuli suite "{{id}}" ran in {{duration}} seconds - {{suite_summary}}. (Last suite run: {{stop_date}})
  sakuli.receiver.gearman.nagios.output.suite.table={{state_short}} Sakuli suite "{{id}}" ran in {{duration}} seconds - {{suite_summary}}. (Last suite run: {{stop_date}})
  sakuli.receiver.gearman.nagios.output.case.ok={{state_short}} case "{{id}}" ran in {{duration}}s - {{state_description}}
  sakuli.receiver.gearman.nagios.output.case.warning={{state_short}} case "{{id}}" over runtime ({{duration}}s /{{state_description}} at {{warning_threshold}}s) {{step_information}}
  sakuli.receiver.gearman.nagios.output.case.critical={{state_short}} case "{{id}}" over runtime ({{duration}}s /{{state_description}} at {{critical_threshold}}s) {{step_information}}
  sakuli.receiver.gearman.nagios.output.case.error={{state_short}} case "{{id}}" {{state_description}}: {{error_message}}
  ```
 
The HTML table in status information field will be generated after following structure:
 
 ```
 sakuli.receiver.gearman.nagios.output.
 |- suite.summary  <-> complete status summary for the hole suite
 |- suite.table    <-> short state description of the suite
 |--- case.ok || case.warning || case.critical || case.error
      <-> 
      status info for each case in dependency of his state (ok, warning, critical, error)
      
 ```
 
To customize your messages you can use a set of placeholders (`{{placeholder_name}}). The placeholders will be filled
with current information of Sakuli test suite, before the data is sent to the gearman server. If any of the infromation
is not available the value will be an empty String. The following placeholders currently usable:
  
* __scope based placeholders__  -  will be filled with the information from the test suite or from the case in dependency of the message type:
    * `{{state}}` state in uppercase like _OK_ or _WARNING__   
    * `{{state_short}}` short marker for the state like _[OK]_ or _[WARN]_  
    * `{{state_description}}` short description of the state like _ok_ or _warning in step_ 
    * `{{id}}` identifier of the suite or the case 
    * `{{NAME}}`name of the suite or the case 
    * `{{duration}}` duration in seconds  
    * `{{start_date}}` date when execution started, formated as string `dd.MM HH:mm:ss` 
    * `{{stop_date}}` date when execution stopped, formated as string `dd.MM HH:mm:ss` 
    * `{{error_message}}` exception message log 
    * `{{warning_threshold}}` warning threshold of the runtime in seconds 
    * `{{critical_threshold}}` critical threshold of the runtime in seconds 
* __suite specific placeholders__  -  will be only filled in `sakuli.receiver.gearman.nagios.output.suite.XXX` messages:
    * `{{suite_summary}}` short summary with all important information of the current test suite execution 
    * `{{suite_folder}}` path to the used test suite folder 
    * `{{browser_info}}` detailed information about the used browser
    * `{{host}}` host name where the test has been executed
* __case specific placeholders__  -  will be only filled in `sakuli.receiver.gearman.nagios.output.case.XXX` messages:
    * `{{case_file}}` path and filename of the test case file like `path-of-suite/test-case/_tc.js` 
    * `{{case_start_URL}}` URL of the first visited website at the case  
    * `{{case_last_URL}}` URL of the last visited website at the case 
    * `{{step_information}}` information about the executed steps with warnings (steps inside of there warning threshold, won't be added) 

## Configure the nagios server
TODO Simon
### Install the sakuli plugin
### Add an passive service check for each test suite
