## PNP4Nagios
### RRD Storage Type

In PNP4Nagios RRD Storage type "MULTIPLE" is of great importance for Sakuli checks, because the number of steps can change by time (=if you are adding/deleting some).

Verify `RRD_STORAGE_TYPE` in `process_perfdata.cfg`: 

	OMD[sakuli]:~$ vim ~/etc/pnp4nagios/process_perfdata.cfg
	  RRD_STORAGE_TYPE = MULTIPLE

If this value is *"SINGLE"* on your system and you do not want to change it globally, use the *custom check_command* cfg file. PNP4Nagios will then use storage type *"MULTIPLE"* only for this check_command then:  

	OMD[sakuli]:~$ cp __TEMP__/sakuli-vx.x.x-SNAPSHOT/setup/nagios/check_sakuli.cfg ~/etc/pnp4nagios/check_commands/

### RRD heartbeat

Each RRD file contains a heartbeat value, which determines how much time must pass without any new update, before RRDtool writes an UNKNOWN value (nan) into the data slot (the graph will have a gap then). In PNP4nagios heartbeat is defined at approx. 2 1/2 hours. If your Sakuli check runs only every 2 hours, this value will be fine. But for a 5 minute interval, this is way too long. As a consequence, the graph line will be continuously drawed even Sakuli did no check for two hours. Hence, always make sure to adapt the heartbeat to a value which is slightly higher than the interval of Sakuli checks (and indeally the same as [freshness_threshold](integration/monitoring/forwarder-gearman.md#create-a-nagios-service), if you use the gearman receiver): 

    OMD[sakuli]:~$ cd ~/var/pnp4nagios/perfdata/sakulihost/
    # Sakuli check interval: 2 minutes --> RRD heartbeat 3 minutes
    OMD[sakuli]:~$ for file in sakuli_e2e_webshop*.rrd; do rrdtool tune $file --heartbeat 1:180; done

### install PNP graph template

Copy the PNP4nagios graph template into the templates folder: 

	OMD[sakuli]:~$ cp __TEMP__/sakuli-vx.x.x-SNAPSHOT/setup/nagios/check_sakuli.php ~/etc/pnp4nagios/templates/


### Include CPU/Memory metrics in Sakuli graphs (optional) 

If Sakuli reports a long check runtime, it is good to know the CPU/Memory metrics on the Sakuli client machine, because CPU/IO bottlenecks affect Sakuli tests, too.
 
The following optional enhancement displays the **CPU/Memory graph** lines of the Sakuli test client in the suite/case graph. By setting **custom host macros**, the graph template knows where to fetch these data from. 

![PNP graph](./images/pnp_graph.png) 
*(picture shows a Linux client with CPU check, displayed by a yellow line)*

#### add CPU load check (for Linux Sakuli clients)

Add this **command** to `commands.cfg`: 

    define command{
        command_name    check_local_load
        command_line    $USER1$/check_load -w $ARG1$ -c $ARG2$
    }

Add this **service** to `services.cfg`:  

	define service {
	  service_description            CPU_Load
	  host_name                 sakuli_client
	  use                            generic-service,srv-pnp
	  # if Sakuli checks are running on the same machine (as in the demo VM)
	  check_command                  check_local_load!2.5,1.5,1!5,3.5,2
	  # if Sakuli checks are running on another host than OMD
	  check_command                  check_by_ssh!check_load!2.5,1.5,1!5,3.5,2
	}

Add this **custom host macros** to every Sakuli host in `hosts.cfg`:
	
	define host {
        …
	    _E2E_CPU_HOST                  sakuli_client
	    _E2E_CPU_SVC                   CPU_Load_load5
    }
   
Now reload OMD: 

    omd reload   
    
You should see now the following service on `sakuli_client`: 

![PNP graph](./images/svc_cpu.png)  
   
**Note**: The value of `_E2E_CPU_SVC` and `_E2E_MEM_SVC` refer to the file name of the corresponding RRD file. `CPU_Usage_5` for example means to get the the CPU usage data from `$OMD_ROOT/var/pnp4nagios/perfdata/[_E2E_CPU_HOST]/CPU_Usage_5.rrd`.    
    
#### add CPU/Memory usage check (for Windows Sakuli clients)

Install **NSClient++** on the Windows client. Then add this **command check_nrpe_arg:**

		vim ~/etc/nagios/conf.d/commands.cfg
		
		define command {
		  command_name                   check_nrpe_arg
		  command_line                   $USER1$/check_nrpe -H $HOSTADDRESS$ -c $ARG1$ -a $ARG2$
		}

Then add these **services** to Nagios: 

		define service {
		  service_description          CPU_Usage
		  host_name                 	win7sakuli
		  use                          generic-service,srv-pnp
		  check_command                check_nrpe_arg!CheckCPU!warn=80% crit=90% time=15 time=5 time=1 ShowAll
		}

		define service {
		  service_description          Mem_Usage
		  host_name                 	win7sakuli
		  use                          generic-service,srv-pnp
		  check_command                check_nrpe_arg!CheckMem!MaxWarn=80% MaxCrit=90% ShowAll type=page type=paged type=physical type=virtual
		}
 
Add these host macros to every Nagios host where Sakuli checks are defined: 

		_E2E_CPU_HOST                  win7sakuli
		_E2E_CPU_SVC                   CPU_Usage_5
		_E2E_MEM_HOST                  win7sakuli
		_E2E_MEM_SVC                   Mem_Usage

Now reload OMD: 

    omd reload	
		
**Note**: The value of `_E2E_CPU_SVC` and `_E2E_MEM_SVC` refer to the file name of the corresponding RRD file. `CPU_Usage_5` for example means to get the the CPU usage data from `$OMD_ROOT/var/pnp4nagios/perfdata/[_E2E_CPU_HOST]/CPU_Usage_5.rrd`. 

#### XML update delay

As soon as the created services produce perfdata for the first time, their XML file created by PNP4Nagios will also contain the host macros created in the step before. If not, check if  `XML_UPDATE_DELAY` in `etc/pnp4nagios/process_perfdata.cfg` is set too high. 

#### Change PNP working mode

OMD runs PNP by default in **[Bulk Mode with NPCD and npcdmod.o](http://docs.pnp4nagios.org/pnp-0.6/modes#bulk_mode_with_npcdmod)**. In this mode the Nagios broker module `npcdmod.o` reads the performance directly from the monitoring core and writes them in *var/spool/perfdata*. This data are not expandable with **custom macros** - therefore the mode has to be changed to **[Bulk Mode with NPCD](http://docs.pnp4nagios.org/pnp-0.6/modes#bulk_mode_with_npcd)**. (the performance of both modes will be equal). 

In this mode the monitoring core itself writes perfdata to the spool directory (instead of *npcdmod.o*). The format of this data can be freely defined by adapting `service_perfdata_file_template`. In the following code block you can see that the four **custom host macros** were added to this template string. Perfdata files are then moved to *var/spool/perfdata* every 15 seconds by the monitoring core.

**Make sure to replace the OMD site name placeholder `__OMD_SITE__` with your site name!** (in *vim* type `:%s/__OMD_SITE__/yoursitename/g`)

	vim ~/etc/nagios/nagios.d/pnp4nagios.cfg

	process_performance_data=1
		
	# COMMENT THE LINE BELOW
	# broker_module=/omd/sites/__OMD_SITE__/lib/npcdmod.o config_file=/omd/sites/__OMD_SITE__/etc/pnp4nagios/npcd.cfg
	
	# services
	service_perfdata_file=/omd/sites/__OMD_SITE__/var/pnp4nagios/service-perfdata
	service_perfdata_file_template=DATATYPE::SERVICEPERFDATA\tTIMET::$TIMET$\tHOSTNAME::$HOSTNAME$\tSERVICEDESC::$SERVICEDESC$\tSERVICEPERFDATA::$SERVICEPERFDATA$\tSERVICECHECKCOMMAND::$SERVICECHECKCOMMAND$\tHOSTSTATE::$HOSTSTATE$\tHOSTSTATETYPE::$HOSTSTATETYPE$\tSERVICESTATE::$SERVICESTATE$\tSERVICESTATETYPE::$SERVICESTATETYPE$\tE2ECPUHOST::$_HOSTE2E_CPU_HOST$\tE2ECPUSVC::$_HOSTE2E_CPU_SVC$\tE2EMEMHOST::$_HOSTE2E_MEM_HOST$\tE2EMEMSVC::$_HOSTE2E_MEM_SVC$
	service_perfdata_file_mode=a
	service_perfdata_file_processing_interval=15
	service_perfdata_file_processing_command=omd-process-service-perfdata-file
		
	# hosts
	host_perfdata_file=/omd/sites/__OMD_SITE__/var/pnp4nagios/host-perfdata
	host_perfdata_file_template=DATATYPE::HOSTPERFDATA\tTIMET::$TIMET$\tHOSTNAME::$HOSTNAME$\tHOSTPERFDATA::$HOSTPERFDATA$\tHOSTCHECKCOMMAND::$HOSTCHECKCOMMAND$\tHOSTSTATE::$HOSTSTATE$\tHOSTSTATETYPE::$HOSTSTATETYPE$
	host_perfdata_file_mode=a
	host_perfdata_file_processing_interval=15
	host_perfdata_file_processing_command=omd-process-host-perfdata-file

Check if the perfdata processing commands are present:

	vim ~/etc/nagios/conf.d/pnp4nagios.cfg

	define command{
		command_name    omd-process-service-perfdata-file
		command_line    /bin/mv /omd/sites/__OMD_SITE__/var/pnp4nagios/service-perfdata /omd/sites/__OMD_SITE__/var/pnp4nagios/spool/service-perfdata.$TIMET$
	}
 
	define command{
		command_name    omd-process-host-perfdata-file
		command_line    /bin/mv /omd/sites/__OMD_SITE__/var/pnp4nagios/host-perfdata /omd/sites/__OMD_SITE__/var/pnp4nagios/spool/host-perfdata.$TIMET$
	}

Restart the OMD site to unload the *npcdmod.o* module:

    omd restart
    
#### Test
 
First reschedule the CPU/Mem check on the sakuli client. It can take several minutes to store the values in the RRD database. As soon as you can see "real" values in the PNP4Nagios graph of "CPU Load" (instead of "`-nan`"), restart the Sakuli check. 
The Sakui graph should now contain also CPU/Memory values. 
