# Gearman Receiver
This page describes how Sakuli can be configured to transmit its test results directly into the **Gearman queue** of the monitoring system. 

![sakuli-db-receiver](pics/sakuli-gearman.png)

The configured `testsuite.id` in the `testsuite.properties` file, will be used as __service name__.
The results (ok and not ok results) will be available in the __"Status Information"__ of the service check.  

## OMD Configuration

### Enable the site's mod-gearman server

Stop the OMD site:

	OMD[sakuli]:~$ omd stop

Start the OMD configuration menu

	OMD[sakuli]:~$ omd config
	
Select *Distributed Monitoring* and

* -> `MOD_GEARMAN` -> `ON` 
* -> `GEARMAND_PORT` -> `__OMD_IP__:__GEARMAN_PORT__` (default: 4470) 
* -> `GEARMAN_WORKER` -> `OFF`

As Sakuli only uses the *result queue* of gearmand, you can disable all other queues unless you are using mod-gearman for regular checks: 

    OMD[sakuli]:~$ vim ~/etc/mod-gearman/server.cfg
	eventhandler=no
	services=no
	hosts=no
	do_hostchecks=no
	
At the time of the creation of this documents, Sakuli does not encrypt any gearman results. Therefore, set  `accept_clear_results` in ``:

    OMD[sakuli]:~$ vim ~/etc/mod-gearman/server.cfg
	accept_clear_results=yes
	
Reload gearmand to load the new settings: 

    OMD[sakuli]:~$ omd reload gearmand
	  
### Create a Nagios service

<!--- fixme -->
Create a check_command, which will be executed only if Nagios did not receive a Sakuli result within the last 30 minutes. This ensures that you get a notification even if no passive check results arrive in Nagios at all:   

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

![omd_pending2](pics/omd-pending2.png)

The check is waiting now for check results from Sakuli clients. 




## Sakuli Configuration
Open `__SAKULI_HOME__\_include\sakuli.properties` on the Sakuli client: 

### Gearman parameters

	sakuli.receiver.gearman.enabled=true
	sakuli.receiver.gearman.server.host=__GEARMAN_IP__
	sakuli.receiver.gearman.server.port=[Gearman Port defined in "omd config"]
	sakuli.receiver.gearman.server.queue=check_results
	
	# Nagios check settings
	# default nagios host_name (can be overwritten in testsuite.properties) 
	sakuli.receiver.gearman.nagios.hostname=win7sakuli
	# gets appended to the performance data in order to set the name of PNP4nagios template
	sakuli.receiver.gearman.nagios.check_command=check_sakuli

