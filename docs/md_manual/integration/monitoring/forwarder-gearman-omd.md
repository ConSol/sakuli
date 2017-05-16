# Gearman Forwarder
This page describes how the results of the Sakuli tests **example_windows7/ubuntu/opensuse** can be transmitted directly into the **Gearman result queue** of the monitoring system. 

![sakuli-db-forwarder](images/sakuli-gearman.png)

* Nagios **host**: property `sakuli.forwarder.gearman.nagios.hostname` (define globally or per suite)
* Nagios **service**: property `testsuite.id` in `testsuite.properties` 

## OMD Configuration

### Enable the site's mod-gearman server

Stop the OMD site:

	OMD[sakuli]:~$ omd stop

Start the OMD configuration menu

	OMD[sakuli]:~$ omd config
	
Select *Distributed Monitoring* and

* -> `GEARMAND` -> `ON` 
* -> `GEARMAND_PORT` -> `__OMD_IP__:__GEARMAN_PORT__` (default: 4730) 
* -> `GEARMAN_NEB` -> `ON` 
* -> `GEARMAN_WORKER` -> `OFF` # only if workers aren't still used
* -> `MOD_GEARMAN` -> `ON` 

As Sakuli only uses the *result queue* of gearmand, you can disable all other queues unless you are using mod-gearman for regular checks: 

    OMD[sakuli]:~$ vim ~/etc/mod-gearman/server.cfg
	eventhandler=no
	services=no
	hosts=no
	do_hostchecks=no
	
At the time of the creation of this documents, Sakuli does not encrypt any gearman results by default. Therefore, set  `accept_clear_results` in `server.cfg`:

    OMD[sakuli]:~$ vim ~/etc/mod-gearman/server.cfg
	accept_clear_results=yes
	
Restart OMD: 

    OMD[sakuli]:~$ omd start
	  
### Create a Nagios service

Create a check_command, which will be executed only if Nagios did not receive a Sakuli result within the last 30 minutes. This ensures that you get a notification even if no passive check results arrive in Nagios at all:   

	vim ~/etc/nagios/conf.d/commands.cfg
	
	define command {
	  command_name                   check_dummy
	  command_line                   $USER1$/check_dummy $ARG1$ $ARG2$
	}


Create a host object for the Sakuli client: 

    OMD[sakuli]:~$ vim etc/nagios/conf.d/hosts.cfg
	
	define host {
	  host_name                      sakuli_client
	  alias                          Sakuli Client
	  address                        [IP]
	  use                            generic-host
	}

Create the following service object for the first test case. *freshness_threshold* should be slightly higher than the interval Sakuli tests are planned (see also [RRD heartbeat](advanced-topics/installation-omd.md#rrd-heartbeat) )

	vim ~/etc/nagios/conf.d/services.cfg

    OMD[sakuli]:~$ vim etc/nagios/conf.d/services.cfg

	define service {
	  # service_description            example_windows7
	  # service_description            example_opensuse
	  service_description            example_ubuntu
	  host_name                      sakuli_client
	  use                            generic-service,srv-pnp
	  active_checks_enabled          0
	  check_command                  check_dummy!3!'Did not receive any Sakuli result within 3 minutes.'
	  check_freshness                1
	  freshness_threshold            180
	  passive_checks_enabled         1
	}
	
Reload OMD:

	omd reload
	
Now open Thruk; you should see now the Sakuli host with one service attached: 

![omd_pending2](images/omd-pending2.png) 

The check is waiting now for check results from a Sakuli client. 

## Sakuli gearman forwarder configuration

On the Sakuli client you must set the global properties for the gearman receiver. For this, edit `sakuli.properties` in the folder containing the test suites (you can copy the lines from `__SAKULI_HOME__/conf/sakuli-default.properties`):. 

    __INST_DIR__/example_test_suites/sakuli.properties:

	sakuli.forwarder.gearman.enabled=true
	sakuli.forwarder.gearman.encryption=false
	sakuli.forwarder.gearman.secret.key=secret_password
	sakuli.forwarder.gearman.server.host=__GEARMAN_IP__
	sakuli.forwarder.gearman.server.port=[Gearman Port defined in "omd config" (default:4730)]
	sakuli.forwarder.gearman.server.queue=check_results
	
	# Nagios host where all Sakuli services are defined on. If neccessary, override this value per test suite. 
    # (Nagios service name is defined by testsuite.properties -> suiteID)
	sakuli.forwarder.gearman.nagios.hostname=sakuli_client
	sakuli.forwarder.gearman.nagios.check_command=check_sakuli

## Using AES encryption (optional)

The gearman forwarder supports AES encryption when sending checked results to the OMD server. The AES encryption uses a 32 byte (256 bit) secret key that
has to be given in the properties. 

    sakuli.forwarder.gearman.encryption=true
    sakuli.forwarder.gearman.secret.key=secret_password

In case you get a `java.lang.security.InvalidKeyException` with error message *"Illegal key size or default parameters"* you probably
need to enable unlimited strength security policies in your Java JRE. This is done by adding a special security policy JAR to the Java JRE lib directory. For the Java JRE 8, take a look at [Oracle - Java Cryptography Extension 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).

On the server side (OMD) you have to enable the encryption feature of [mod-gearman](https://labs.consol.de/nagios/mod-gearman/). Therefore the following two steps are necessary:

1) Set the server side encryption password:

     OMD[sakuli]:~$ echo "secret_password" > ~/etc/mod-gearman/secret.key

2) Enable the `encryption` and disable `accept_clear_results` in the config file:

	 OMD[sakuli]:~$ vim ~/etc/mod-gearman/server.cfg
	
	encryption=yes
    accept_clear_results=yes


## Test result transmission to OMD

Execute the example test case again:

* **Ubuntu**: `sakuli run __INST_DIR__/example_test_suites/example_ubuntu/` 
* **openSUSE**: `sakuli run __INST_DIR__/example_test_suites/example_opensuse/` 
* **Windows7**: `sakuli run __INST_DIR__\example_test_suites\example_windows7\`
* **Windows8**: `sakuli run __INST_DIR__\example_test_suites\example_windows8\`

The service should change its status to:

![omd_pending2](images/omd-ok.png) 


![omd_pending2](images/omd-ok-details.png) 


