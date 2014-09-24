# Installation guide for Sakuli under Windows 7
You probably came from the page "Introduction" - if not, and if you are nor sure what Sakuli is, please read first [README](../README.md). 

## Prerequisites
The following installation manual assumes that...

* you have a fresh installed Windows 7 (no matter if Home/Professional/whatever) machine in front of you. 
* all OS updates are installed
* this machine has access to the internet
* the local firewall is disabled
* there is a user account "sakuli" with admin rights
* you have finished the [OMD Preparation](installation-omd.md) instructions

We recommend to run Sakuli clients on virtual machines, as they are easy to manage. 

## Preparations
The steps in [Sakuli Client Troubleshooting ("How to make tests more reliable")](./docs/troubleshooting-sakuli-client.md) are optional (Sakuli will run even without doing them), but will improve the check quality/reliability. 

## Installation of Sakuli
This chapter includes the installation of Sakuli (which already has Sikuli on board) as well as the installation of Sahi. 
### Java JRE
* Install Java7 JRE. (Make sure that you do **not install ASK toolbar**, which is enabled by default!)
* Modify PATH: 
 * From the desktop, right-click *My Computer* and click *Properties*
 * In *System Properties*, click on *Advanced*
 * Select the `Path` variable in the Systems Variable section and click *Edit*. Add the following line to the very end, apply and reboot: 
	
	```
	%ProgramFiles(x86)%\Java\jre7\bin
	```
	

### Sakuli - Install the sakuli-zipped-release
* Download [sakuli-zipped-release-v0.4.2.zip](http://labs.consol.de/sakuli/install/sakuli-zipped-release-v0.4.2.zip)
* Unzip the downloaded archive to `C:\\` as destination folder (=it will decompress into `C:\sakuli`). 
* From the desktop, right-click *My Computer* and click *Properties*
	* In *System Properties*, click on *Advanced*
	* Create a new user variable **%SAKULI_HOME%**: 
		* Name: `SAKULI_HOME`
		* Value: `C:\sakuli`
	* Create a new user variable **%PATH%**: 
		* Name: `PATH`
		* Value: `%SAKULI_HOME%\bin\lib\libs`
* Reboot the machine


**Alternative download:** Create a local Sakuli repository by cloning from [https://github.com/ConSol/sakuli/](https://github.com/ConSol/sakuli/).

### Configuration

Some configuration settings in the file `%SAKULI_HOME%\_include\sakuli.properties`: 

#### Encryption:
  * To make usage of the Sakuli encryption feature, set the encryption interface property like `sakuli.encryption.interface=eth3`. For further informations jump to [encryption of secrets](sakuli-manual.md) and come back here.
  * If there is no need of any encryption feature, set the property `sakuli.encryption.interface.testmode=true`.

#### Receivers

Sakuli can send test result to "Receivers", which can be currently GearmanD servers (such as Nagios monitoring systems with mod-gearman) and JDBC databases. If no receiver is defined, a result summary is printed out in the end of a suite. 

Depending on your environment, you probably want to set up on of these two possible receiver types. 

![sakuli_receivers](../docs/pics/sakuli-receivers.png)

##### Database receiver
Set up your database connection information like:

	jdbc.driverClass=com.mysql.jdbc.Driver
	# host for the JDBC database
	jdbc.host=localhost
	# JDBC-Database port:
	jdbc.port=3306
	## name of the database
	jdbc.database=sahi
	## database username
	jdbc.user=sahi
	## database password
	jdbc.pw=sahi
	## pattern for JDBC database connection URL
	jdbc.url=jdbc:mysql://${jdbc.host}:${jdbc.port}/${jdbc.database}

##### Gearman receiver
=======
* Receivers (optional, disabled per default):

  To interpret the results of an test execution, it is possible to configure one or more of the following receivers:  
  * [Database Receiver](receivers/database.md)
  * [Gearman Receiver](receivers/gearman.md)
     

	sakuli.receiver.gearman.enabled=true
	sakuli.receiver.gearman.server.host=gearman-server-host
	sakuli.receiver.gearman.server.port=4730
	sakuli.receiver.gearman.server.queue=check_results
	sakuli.receiver.gearman.nagios.hostname=sakuli-test-client
	sakuli.receiver.gearman.nagios.check_command=check_sakuli_db_suite
	
	sakuli.receiver.gearman.nagios.output.suite.summary= 


#### Company proxy	
(optional) Configure your company proxy like described under [Sakuli-Manual - Proxy-Settings](sakuli-manual.md#proxy-settings)

#### Others 
Some further default configuration can be done, see therefore the comments in [sakuli.properties](../core/src/main/_include/sakuli.properties). This default settings will be valid for all test suites of the Sakuli installation. Each single property can be overridden in the property file `testsuite.properties` of the test suite folders as well, see e.g. [testsuite.properties](../sakuli_test_suites/example/testsuite.properties). Overriding of properties makes it possible to configure the behaviour of each test suite individually.

	
### Sahi

* Download the latest version of Sahi from [http://sourceforge.net/projects/sahi/files/latest/download?source=files](http://sourceforge.net/projects/sahi/files/latest/download?source=files)
* Unpack the downloaded file and start the installation by double clicking on `install_sahi_v44_20130429.jar`.
	* Installation path: `%SAKULI_HOME%\sahi`
	* select all packages to install

Now it's time to start the Sahi controller for the first time. Open "Start Sahi" from your desktop or from the start menu. 

![startsahi](../docs/pics/w_startsahi.jpg) 	

The Dashboard should list now all available browsers on this system: 

![db_browsers](../docs/pics/w_sahi_dashboard_browsers.jpg) 	
Click on any browser you like; Sahi will start it and present the default start page: 

![sahi_start](../docs/pics/sahi_startpage.jpg) 

If neccessary, define now your company's proxy within Sahi: [Proxy settings](../docs/sakuli-manual.md)

Congratulations; Sahi is now installed completely!

(Otherwise, see [Troubleshooting](../docs/troubleshooting-sakuli-client.md)  )

### PhantomJS (Optional)
	
Currently, each Sakuli test will start a browser, even for pure Sikuli GUI tests (=where you don't need any browser). In that case, the headless browser *phantomJS* does the trick. 

* Download the latest version of phantomJS from [http://phantomjs.org](http://phantomjs.org)
* Open the ZIP file and copy `phantomjs.exe` to `%SAKULI_HOME%\phantomjs` (create that folder)
* Save [sahi.js](http://labs.consol.de/sakuli/install/3rd-party/phantom/sahi.js) into `%SAKULI_HOME%\phantomjs`

<pre>
	&lt;browserType&gt; 
		&lt;name&gt;phantomjs&lt;/name&gt; 
		&lt;displayName&gt;PhantomJS&lt;/displayName&gt; 
		&lt;icon&gt;safari.png&lt;/icon&gt; 
		&lt;path&gt;C:\sakuli\phantomjs\phantomjs.exe&lt;/path&gt; 
		&lt;options&gt;--proxy=localhost:9999 C:\sakuli\phantomjs\sahi.js&lt;/options&gt; 
		&lt;processName&gt;phantomjs.exe&lt;/processName&gt; 
		&lt;capacity&gt;100&lt;/capacity&gt; 
		&lt;force&gt;true&lt;/force&gt; 
    &lt;/browserType&gt;
</pre>

Now it's time to setup the first Sakuli check: go to [First steps on Windows 7](../docs/firststeps-windows.md).

## Additional tools
### Browser 
Install any of your desired browsers (Firefox, Chrome, Opera, …). To implement the sakuli_demo test case, install at least firefox. 
### Greenshot 
To take screenshots which should be used by Sikuli, you need a handy screenshot capturing tool. We highly recommend the installation of [Greenshot](http://www.getgreenshot.org), but any other tool which is able to save screenshots as JPG/PNG is possible, too. 
### Notepad++
Install an advanced text editor to edit Sakuli test cases. We recommend [Notepad++](http://notepad-plus-plus.org/).
	
