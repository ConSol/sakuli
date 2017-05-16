# Sakuli: Integration in OMD
This chapter describes all neccessary steps to configure a **Nagios** compatible monitoring system to receive Sakuli test results. OMD with Thruk as web frontend is highly recommended, but any other Nagios based system will do also. 

## Requirements
* **[OMD](http://www.omdistro.org) 1.x** installed on a Linux operating system 
* a running **OMD site** (here: **sakuli**)

## Download

General note: you should download the **same package version of Sakuli** as you did on the clients. **Do not mix versions**. 

* Download **Sakuli** from  [http://labs.consol.de/sakuli/install](http://labs.consol.de/sakuli/install) into a temporary folder `__TEMP__`. 

        cd __TEMP__
        wget http://labs.consol.de/sakuli/install/sakuli-vx.x.x-SNAPSHOT.zip
        
* decompress it:

		unzip sakuli-vx.x.x-SNAPSHOT.zip

## General preparations
All following steps should be done as the **OMD site user** (here: "sakuli"):

		su - sakuli
		
### Nagios

Sakuli will produce HTML formatted output. **HTML escaping** in Nagios must be turned off: 

	OMD[sakuli]:~$ vim etc/nagios/cgi.cfg
		escape_html_tags=0


## Choose a forwarder
Depending on your environment, you can set up on of these two possible forwarder types. Each of them is documented on a single page.

  * [Setting up Nagios to **receive Gearman results** from Sakuli clients](integration/monitoring/forwarder-gearman.md#omd-configuration) (**recommended**)
    * passive check (via Gearmand result queue) 
    * \+ get results immediately
    * \+ PNP graphs
    * \- no performance value history to do further reporting (despite reading the RRDs again; not recommended) 
  * [Setting up the Sakuli **result database** in OMD](integration/monitoring/forwarder-database.md#omd-configuration) and let Nagios check the database
    * active check (against MySQL result database)
    * \- results in Nagios always lag behind
    * \+ PNP graphs
    * \+ all suite/case/step performance values are stored in database and can be used for further reporting

TODO mention PNP + deliverd default templates