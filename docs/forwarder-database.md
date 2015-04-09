# Database Forwarder
This page describes how Sakuli can be configured to write its test results into a **MySQL database** which is checked asynchronously by the monitoring system with *check_mysql_health*. 

![sakuli-db-forwarder](pics/sakuli-db.png)
 
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

	OMD[sakuli]:~$ mysql < __TEMP__/setup/database/create_sakuli_database.sql
	
Create the **database user**:

	OMD[sakuli]:~$ mysql
	  grant ALL on sakuli.* to '__DB_USER__'@'%' identified by '__DB_PASSWORD__';
	  flush privileges;
	  quit
	  
Check the connection with *check_mysql_health*: 

	OMD[sakuli]:~$ 	~/lib/nagios/plugins/check_mysql_health -H __DB_IP__ --username __DB_USER__ --password __DB_PASSWORD__ --database sakuli --port __DB_PORT__ --mode connection-time
	  OK - 0.24 seconds to connect as sakuli | connection_time=0.2366s;1;5

### create Nagios check

Nagios fetches Sakuli check results using the plugin [check_mysql_health](http://labs.consol.de/lang/de/nagios/check_mysql_health/), which is already contained in OMD. 

#### CheckMySQLHealthSakuli.pm

The Perl module `CheckMySQLHealthSakuli.pm` enhances the functionality of *check_mysql_health* by introducing the mode `--my-sakuli-suite`. 

Create a config directory for *check_mysql_health* and **copy the module** there: 

	OMD[sakuli]:~$ mkdir ~/etc/check_mysql_health
	OMD[sakuli]:~$ cp __TEMP__/setup/nagios/CheckMySQLHealthSakuli.pm ~/etc/check_mysql_health/

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
	  $USER16$=__DB_IP__  
	  
##### Nagios configuration
Create a new **check_command**: 

	vim ~/etc/nagios/conf.d/commands.cfg
	
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

Create a **host object** for the host on which Sakuli checks will be executed: 

	vim ~/etc/nagios/conf.d/hosts.cfg
	
	define host {
	  host_name                      win7sakuli
	  alias                          Windows 7 Sakuli
	  address                        __CLIENT_IP__
	  use                            generic-host
	}

Create the following **service object** for the first test case: 
<!--- fixme Minimalbeispiel -->

	vim ~/etc/nagios/conf.d/services.cfg
	
	define service {
	  service_description            sakuli_demo
	  host_name                      win7sakuli
	  use                            generic-service,srv-pnp
	  check_command                  check_sakuli!sakuli_demo!3600
	}
	
Reload OMD:

	omd reload
	
Now open Thruk; you should see now the Sakuli host with one service attached: 

![omd_pending](pics/omd-pending.png)

Re-scheduling this service should display the UNKNOWN message that the requested suite could not be found. For the moment, this is ok: 

![omd_unknown](pics/omd-unknown.png)


### Database cleanup (optional)

Create a OMD crontab entry for a automatic database cleanup: 

    OMD[sakuli]:~$ vim etc/cron.d/sakuli
    
    00 12 * * * $OMD_ROOT/sakuli/scripts/helper/mysql_purge.sh > /dev/null 2>&1 

After that, reload the OMD crontab: 

    OMD[sakuli]:~$ omd reload crontab 
    Removing Crontab...OK
    Initializing Crontab...OK

## Sakuli Configuration

Open `__SAKULI_HOME__/_include/sakuli.properties` on the Sakuli client and fill in the **database parameters** Sakuli should write the test results to:

    # DEFAULT: false
    sakuli.forwarder.database.enabled=true
	
	jdbc.driverClass=com.mysql.jdbc.Driver
    jdbc.host=__DB_IP__
    jdbc.port=__DB_PORT__
    jdbc.database=sakuli
    jdbc.user=__DB_USER__
    jdbc.pw=__DB_PW__
    jdbc.model=sakuli
    jdbc.url=jdbc:mysql://${jdbc.host}:${jdbc.port}/${jdbc.database}

