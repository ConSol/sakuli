# Sakuli: Installation in OMD
This chapter describes all neccessary steps to configure OMD (Open Monitoring Distribution, which contains Nagios, Shinken, Icinga, and Naemon) to monitor Sakuli test cases executed on a remote machine. OMD is highly receommended, but any other Nagios installation will do also. ()The steps may be different then…). 

This chapter handles the configuration of the monitoring core "Nagios"; the settings for Icinga and Shinken may differ slightly. 

## Requirements
* [OMD](http://www.omdistro.org) installed on a Linux distribution of you choice
* a running OMD site (here: *sakuli*)

## Download
* Create a temporary folder (called *TEMP_FOLDER* in the following) somewhere on the OMD machine and change into it
* Download and unzip *sakuli-zipped-release-v0.4.0.zip*:

      wget https://raw.github.com/ConSol/sakuli/master/install/sakuli-zipped-release-v0.4.0-SNAPSHOT.zip
## PNP4Nagios
### RRD Storage Type
Make sure that your RRD files are stored in mode "MULTIPLE". 

Become Site user and verify RRD_STORAGE_TYPE in *process_perfdata.cfg*: 

	su - sakuli
	OMD[sakuli]:~$ vim ~/etc/pnp4nagios/process_perfdata.cfg
	  RRD_STORAGE_TYPE = MULTIPLE


If this value is "SINGLE" on your system and you do not want to change it globally, use the custom check_command cfg file. PNP4Nagios will use storage type "MULTIPLE" only for this check_command then:  

	OMD[sakuli]:~$ cp [TEMP_DIR]/sakuli/setup/nagios/check_sakuli_db_suite.cfg ~/etc/pnp4nagios/check_commands/
	  
Some background information: 

Normally, RRDtool would create one single RRD file for each Nagios service, containing one ore more data source (=performance data "row"). This number of datasources can't be changed after creation. 

Fortunately, PNP4Nagios is able to let RRDtool create one RRD file for each data source. This is of great importance for Sakuli checks, because the number of FIXME steps can change by time (=if you are adding/deleting some). 

### PNP graph template

Copy the PNP4nagios graph template into the templates folder: 

	OMD[sakuli]:~$ cp [TEMP_DIR]/sakuli/setup/nagios/check_sakuli_db_suite.php ~/etc/pnp4nagios/templates/

## Database

### enable Site-MySQL
If not already done for other reasons, a site-specific MySQL instance has to be started. That's the place where all Sakuli clients will store their check results. 

Become site user

	su - sakuli 

Stop the OMD site:

	OMD[sakuli]:~$ omd stop

Start the OMD configuration menu

	OMD[sakuli]:~$ omd config
	
Select *Addons -> MYSQL -> ON* and exit the menu. 

Open *~/.my.cnf* and set the following values: 

	OMD[sakuli]:~$ vim .my.cnf
	# or any other higher
	 port = 3007  
	# bind on all interfaces
	 bind-address = 0.0.0.0 
	# enable network access
	 #skip-networking

Create the system tables for the new database and start OMD afterwards. You should see now OMD coming up with a dedicated MySQL instance: 

	OMD[sakuli]:~$ mysql_install_db 
	OMD[sakuli]:~$ omd start
	 Starting dedicated Apache for site sakuli...OK
  	 Starting MySQL... .OK.
	 Starting rrdcached...OK
	 Starting npcd...OK
	 Starting nagios...OK
	 Initializing Crontab...OK

### create Sakuli DB and user

Create the Sakuli database using the SQL script within the repository: 

	OMD[sakuli]:~$ mysql < [TEMP_FOLDER]/sakuli/setup/database/create_and_drop_database.sql
	
Create the database user which is used by all clients to write their check results: 

	OMD[sakuli]:~$ mysql
	  grant ALL on sahi.* to 'sahi'@'%' identified by 'sahi';
	  flush privileges;
	  quit
	  
Now you should be able to do a first connection test with *check_mysql_health*: 

	OMD[sakuli]:~$ 	~/lib/nagios/plugins/check_mysql_health -H [IP] --username sahi --password sahi --database sahi --mode connection-time
	  OK - 0.24 seconds to connect as sahi | connection_time=0.2366s;1;5

### check_mysql_health setup

To fetch Sakuli's check results from the database, Nagios uses the plugin [check_mysql_health](http://labs.consol.de/lang/de/nagios/check_mysql_health/), which is already contained in OMD. 

#### CheckMySQLHealthSakuli.pm

*CheckMySQLHealthSakuli.pm* is a Perl module, which enhances the functionality of *check_mysql_health* by introducing a mew mode *--my-sakuli-suite*. 

Create the *etc* directory for *check_mysql_health* and copy the module there: 

	OMD[sakuli]:~$ mkdir ~/etc/check_mysql_health
	OMD[sakuli]:~$ cp [TEMP_FOLDER]/sakuli/setup/nagios/CheckMySQLHealthSakuli.pm ~/etc/check_mysql_health/

#### resource.cfg
Set the USER macros in resource.cfg - of course you can choose other values too (remember then to use the right ones in all other steps).

	OMD[sakuli]:~$ vim etc/nagios/resource.cfg
	  $USER10$=sahi
	  $USER11$=sahi
	  $USER12$=sahi
	  $USER13$=3007
	  $USER15$=/opt/omd/sites/sakuli/etc/check_mysql_health/
	  $USER16$=[IP of OMD]
	  
#### Nagios configuration
Create a new check_command: 

	# check_command for Sakuli 
	# --name = Suite ID
	# --name2 = number of seconds the last result is allowed to be old
	define command {
	  command_name                   check_sakuli_db_suite
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

Create a new service (e.g. on host "win7sakulidemo"):

	define service {
	  service_description            sakuli_demo
	  host_name                      win7sakulidemo
	  use                            generic-service,srv-pnp
	  check_command                  check_sakuli_db_suite!sakuli_demo!3600
}