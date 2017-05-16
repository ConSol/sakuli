# Icinga2 API Forwarder

This page describes how the results of Sakuli tests can be sent to the [REST API](http://docs.icinga.org/icinga2/snapshot/doc/module/icinga2/chapter/icinga2-api) of an [Icinga2](https://www.icinga.org/) monitoring instance. 

![sakuli-icinga2](images/sakuli-icinga2.png)

## Icinga2 configuration

### Enable the Icinga2 API

The steps to enable the Icinga2 API are described in the [REST API documentation](http://docs.icinga.org/icinga2/snapshot/doc/module/icinga2/chapter/icinga2-api).

### Create a Icinga2 service

Create a **check_command**, which will be executed only if Icinga did not receive a Sakuli result within a certain time. This ensures that you get a notification even if no passive check results arrive in Icinga at all:   

	vim /etc/icinga2/conf.d/commands.conf
	
	object CheckCommand "check_dummy" {
       import "plugin-check-command"
       command = [
         PluginDir + "/check_dummy","$dummy_state$","$dummy_text$"
       ]
       vars.dummy_state = 0
       vars.dummy_text = "Check was successful."
    }

    object CheckCommand "check_sakuli" {
       import "check_dummy"
       vars.dummy_state = 3
       vars.dummy_text = "No passive Sakuli check result received."
    }


Create a **host** object for the Sakuli client: 

    vim /etc/icinga2/conf.d/hosts.conf
	
	object Host "sakuliclient01" {
       import "generic-host"
       address = [IP]
    }

Create the following **service** object for the first test case. *freshness_threshold* should be slightly higher than the interval Sakuli tests are planned (if you are using PNP4Nagios, see also [RRD heartbeat](advanced-topics/installation-omd.md#rrd-heartbeat) )

    object Service "sakuli_demo" {
      import "generic-service"
      host_name = "sakuliclient01"
      check_command = "check_sakuli"
      enable_active_checks = 0
      enable_passive_checks = 1
      enable_flapping = 0
      volatile = 1
      enable_perfdata = 1
    }

Reload Icinga2: 

    service icinga2 reload
       
Now open Icingaweb2; you should see the Sakuli host with the service *"sakuli_demo"* attached: 

![icingaweb2-pending2](images/icingaweb2-pending2.png) 

The check is waiting now for check results from a Sakuli client. 



## Sakuli Icinga2 forwarder parameter

On the Sakuli client you must set the global properties for the Icinga2 receiver. For this, edit `sakuli.properties` in the folder containing the test suites (you can copy the lines from `__SAKULI_HOME__/conf/sakuli-default.properties`):
sakuli.forwarder.icinga2.enabled=false

    sakuli.forwarder.icinga2.api.host=__ICINGA_IP__
    sakuli.forwarder.icinga2.api.port=5665
    sakuli.forwarder.icinga2.api.username=icingasakuli
    sakuli.forwarder.icinga2.api.password=icingasakuli
    sakuli.forwarder.icinga2.hostname=sakuliclient01
    
## Graph settings
Icinga2 integration is very new; we did not yet dive into the configuration of Graphite or Grafana graphs. The only supported graphing solution is PNP4Nagios. Nevertheless you are welcome to contribute graph templates for  Grafana and/or Graphite!

### PNP4Nagios

Set the RRD storage type of PNP to MULTIPLE, which creates one RRD file per perfdata label: 

    echo "RRD_STORAGE_TYPE = MULTIPLE" > /etc/pnp4nagios/check_commands/check_sakuli.cfg

Copy the PNP graph template `check_sakuli.php` from `%SAKULI_HOME%/setup/nagios/` on the client to `/usr/share/nagios/html/pnp4nagios/templates/` on the Icinga2 server. 

### Grafana
tbd

### Graphite
tbd

## Test result transmission to Icinga2

Execute the example test case again:

* **Ubuntu**: `sakuli run __INST_DIR__/example_test_suites/example_ubuntu/` 
* **openSUSE**: `sakuli run __INST_DIR__/example_test_suites/example_opensuse/` 
* **Windows 7**: `sakuli run __INST_DIR__\example_test_suites\example_windows7\`
* **Windows 8**: `sakuli run __INST_DIR__\example_test_suites\example_windows8\`

The service in Icnga2 should change its status to:



![icinga_ok](images/icinga_ok.png)  


