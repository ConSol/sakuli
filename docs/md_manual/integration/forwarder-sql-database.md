TODO: remove OMD stuff and described sql schema and how results will be placed there.

# Database Forwarder
This page describes how the results of the Sakuli tests **example_windows7/ubuntu/opensuse** can be written into a **MySQL database** which is then checked asynchronously by the monitoring system with *check_mysql_health*. 

![sakuli-db-forwarder](images/sakuli-db.png)
 
## OMD Configuration

### Enabling the site's MySQL Database
If not already done for other reasons, a site-specific MySQL instance has to be started. That's the place where all Sakuli clients will store their check results. 

Stop the OMD site and start the OMD configuration menu:

	OMD[sakuli]:~$ omd stop
     ...
	OMD[sakuli]:~$ omd config
	
Select *Addons -> MYSQL -> ON* and exit the menu. 

Open `~/.my.cnf` and set the following values: 

	OMD[sakuli]:~$ vim .my.cnf
	# make sure to choose a free port, e.g. 3007
	port = __DB_PORT__  
	# bind on all interfaces
	bind-address = 0.0.0.0 
	# enable network access
	#skip-networking

Create the system tables for the new database and start OMD afterwards. You should see now OMD coming up with a **dedicated MySQL instance**: 

	OMD[sakuli]:~$ mysql_install_db 
	OMD[sakuli]:~$ omd start
	Starting dedicated Apache for site sakuli...OK
	Starting MySQL... .OK.
	Starting rrdcached...OK
	Starting npcd...OK
	Starting nagios...OK
	Initializing Crontab...
	 
### create Sakuli DB and user

Create the **Sakuli database**:

	OMD[sakuli]:~$ mysql < __TEMP__/sakuli-vx.x.x-SNAPSHOT/setup/database/create_sakuli_database.sql
	
Create the **database user**:

	OMD[sakuli]:~$ mysql
	  grant ALL on sakuli.* to '__DB_USER__'@'%' identified by '__DB_PASSWORD__';
	  flush privileges;
	  quit
	  
Check the connection with *check_mysql_health*: 

	OMD[sakuli]:~$ 	lib/nagios/plugins/check_mysql_health -H __DB_IP__ --username __DB_USER__ --password __DB_PASSWORD__ --database sakuli --port __DB_PORT__ --mode connection-time
	  OK - 0.24 seconds to connect as sakuli | connection_time=0.2366s;1;5

### create Nagios check

Nagios fetches Sakuli check results using the plugin [check_mysql_health](http://labs.consol.de/lang/de/nagios/check_mysql_health/), which is already contained in OMD. 

#### CheckMySQLHealthSakuli.pm

The Perl module `CheckMySQLHealthSakuli.pm` enhances the functionality of *check_mysql_health* by introducing the mode `--my-sakuli-suite`. 

Create a config directory for *check_mysql_health* and **copy the module** there: 

	OMD[sakuli]:~$ mkdir etc/check_mysql_health
	OMD[sakuli]:~$ cp __TEMP__/setup/nagios/CheckMySQLHealthSakuli.pm etc/check_mysql_health/

##### resource.cfg
Set **USER macros** for static vars in `resource.cfg`, which makes it easy to use them in all nagios checks:  

	OMD[sakuli]:~$ vim ~/etc/nagios/resource.cfg
	  # database name
	  $USER10$=sakuli
	  # database user
	  $USER11$=__DB_USER__
	  # database password
	  $USER12$=__DB_PASSWORD__
	  # database port
	  $USER13$=__DB_PORT__
	  # check_mysql_health module dir
	  $USER15$=/opt/omd/sites/sakuli/etc/check_mysql_health/
	  # database IP
	  $USER16$=__MySQL_Database_IP__  
	  
##### Nagios configuration
Create a new **check_command**: 

	OMD[sakuli]:~$ vim etc/nagios/conf.d/commands.cfg
	
	# check_command for Sakuli 
	# --name = Suite ID
	# --name2 = number of seconds the last result is allowed to be old
	define command {
	  command_name                   check_sakuli
	  command_line                   $USER1$/check_mysql_health \
      	--hostname=$USER16$ \
        --database=$USER10$ \
        --username=$USER11$ \
        --password=$USER12$ \
        --mode=my-sakuli-suite \
        --name='$ARG1$' \
        --name2=$ARG2$ \
        --report=html \
        --port=$USER13$ \
        --with-mymodules-dyn-dir=$USER15$
	}

Create a **host object** for Sakuli database checks (the checks are executed on the *local* machine, but belong logically to *sakuli_client*):

	OMD[sakuli]:~$ vim etc/nagios/conf.d/hosts.cfg
	
	define host {
	  host_name                      sakuli_client
	  alias                          sakuli_client
	  address                        __SAKULI_CLIENT_IP__
	  use                            generic-host
	}

Create the following **service object** for the first test case. Note the ARG2 in check_command: the database check will only evaluate the last result if it is max. 180 seconds old. If older, the check will return UNKNOWN. (For comparison: this is equivalent to "freshness_threshold" if you would use the [gearman forwarder](forwarder-gearman.md). In any case, you should set the [RRD heartbeat](installation-omd.md#rrd-heartbeat) to the same value to get a gap in the graph if recent client results are missing. ) 

	OMD[sakuli]:~$ vim etc/nagios/conf.d/services.cfg
	
	define service {
      # service_description            example_windows7
      # service_description            example_opensuse
      service_description            example_ubuntu
	  host_name                      sakuli_client
	  use                            generic-service,srv-pnp
	  check_command                  check_sakuli!sakuli_demo!180
	}
	
Reload OMD:

	omd reload
	
Now open Thruk; you should see now the Sakuli host with one service attached: 

![omd_pending](images/omd-pending.png)

Re-scheduling this service should display the UNKNOWN message that the requested suite could not be found. For the moment, this is ok: 

![omd_unknown](images/omd-unknown.png)


## Sakuli database forwarder parameter

On the Sakuli client you must set the global properties for the database receiver. For this, edit `sakuli.properties` in the folder containing the test suites (you can copy the lines from `__SAKULI_HOME__/conf/sakuli-default.properties`):

    __INST_DIR__/example_test_suites/sakuli.properties:

    # DEFAULT: false
    sakuli.forwarder.database.enabled=true

	#DEFAULT: mysql
	sakuli.forwarder.database.jdbc.driverClass=com.mysql.jdbc.Driver
    sakuli.forwarder.database.host=__DB_IP__
    sakuli.forwarder.database.port=__DB_PORT__
    sakuli.forwarder.database=sakuli
    sakuli.forwarder.database.user=__DB_USER__
    sakuli.forwarder.database.password=__DB_PW__


## Test result transmission to OMD

Execute the example test case again:

* **Ubuntu**: `sakuli run __INST_DIR__/example_test_suites/example_ubuntu/`
* **openSUSE**: `sakuli run __INST_DIR__/example_test_suites/example_opensuse/`
* **Windows 7**: `sakuli run __INST_DIR__\example_test_suites\example_windows7\`
* **Windows 8**: `sakuli run __INST_DIR__\example_test_suites\example_windows8\`

The service should change its status to:

![omd_pending2](images/omd-db-ok.png)


![omd_pending2](images/omd-db-ok-details.png)

## Database cleanup (optional)

Sakuli's database can get very large over time. Use the following database maintenance script to keep only the most recent data. 

    OMD[sakuli]:~$ cp `__SAKULI_HOME__/bin/helper/mysql_purge.sh local/bin/`

Create a OMD crontab entry for a automatic database cleanup of data older than 90 days: 

    OMD[sakuli]:~$ vim etc/cron.d/sakuli
    00 12 * * * $OMD_ROOT/local/bin/mysql_purge.sh 90 > /dev/null 2>&1 

After that, reload the OMD crontab: 

    OMD[sakuli]:~$ omd reload crontab 
    Removing Crontab...OK
    Initializing Crontab...OK

