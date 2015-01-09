# Sakuli client installation 

All steps on this page use `%SAKULI_HOME%` as a placeholder for the installation path of Sakuli: 

- `C:\sakuli` on Windows
- `/home/sakuli/sakuli` on Ubuntu 

## Prerequisites
The following installation manual assumes that...

* you have a fresh installed OS (Windows 7 Home/Professional/whatever or Ubuntu 14.04 LTS Desktop) machine with all updates installed
* at least 3GB RAM are available 
* all necessary drivers are installed (incl. VMWare/Virtualbox guest additions)
* this machine has access to the internet
* no local firewall is running
* there is a user account (e.g. "sakuli") with sudo/admin rights
* no service/application is running which could show dialogues which could disturbe e2e checks (e.g. messages indicating a scheduled antivirus scan, custom software installation procedures, ...)
* you have finished the [OMD Preparation](installation-omd.md) instructions

We recommend to run Sakuli clients on virtual machines, as they are easy to manage. 

## Desktop preparation

FIXME Anker

First prepare your desktop environment as described in [Sakuli Client Troubleshooting ("Desktop preparation")](./troubleshooting-sakuli-client.md). These steps are optional (Sakuli will run even without doing them), but will improve the check quality/reliability. We recommend to do this first and then come back here. 

## Software installation 
### Java 7 JRE

Straightforward for Windows; for Ubuntu, see [Ubuntuusers Wiki](http://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_7#Java-7-JRE).

### Sakuli 

* Download the latest version of Sakuli from  [http://labs.consol.de/sakuli/install](http://labs.consol.de/sakuli/install)
* Unzip the downloaded archive on level up of `%SAKULI_HOME%` (= `C:\` on Windows or `/home/sakuli/`on Ubuntu). 

The following settings have to be set in the file `%SAKULI_HOME%\_include\sakuli.properties`: 

#### Encryption:

  * To make use of the Sakuli encryption feature, set the encryption interface property `sakuli.encryption.interfac`. For further informations jump to [encryption of secrets](sakuli-manual.md) and come back here.
  * If there is no need to encrypt passwords, set the property `sakuli.encryption.interface.testmode=true`.

#### Receivers:

Sakuli can send test result to "Receivers", which can be currently GearmanD servers (such as Nagios monitoring systems with mod-gearman) and JDBC databases. If no receiver is defined, a result summary is printed out in the end of a suite. 
  
![sakuli_receivers](../docs/pics/sakuli-receivers.png)

For the configuration of receivers on the OMD server side, see [Receivers in OMD](installation-omd.md#receivers)

Depending on your environment, you probably want to set up one of these two possible receiver types. 

  * [Setting up Sakuli to send results to the Database](receivers/database.md#sakuli-configuration)
  * [Setting up Sakuli to submit results to the Gearman Receiver](receivers/gearman.md#sakuli-configuration)

#### Company proxy:

(optional) Configure your company proxy as described under [Sakuli-Manual - Proxy-Settings](sakuli-manual.md#proxy-settings)

#### Other settings: 

Some further default configuration can be done, see therefore the comments in [sakuli.properties](../core/src/main/_include/sakuli.properties). This default settings will be valid for all test suites of the Sakuli installation. Each single property can be overridden in the property file `testsuite.properties` of the test suite folders as well, see e.g. [testsuite.properties](../sakuli_test_suites/example/testsuite.properties). Overriding of properties makes it possible to configure the behaviour of each test suite individually.

	
### Sahi

* Download the latest version of Sahi from [http://sourceforge.net/projects/sahi/files/latest/download?source=files](http://sourceforge.net/projects/sahi/files/latest/download?source=files)
* Unpack the downloaded file and start the installation by executing `java -jar install_sahi_v44_20130429.jar`, respectively by double clicking this file on Windows. 
	* Installation path: `%SAKULI_HOME%\sahi`
	* select all packages to install

Sahi can be started now for the first time. Open "Start Sahi" from "Applications - Programming" on Ubuntu, or by double clicking the icon on the Windows desktop. 

![startsahi](../docs/pics/w_startsahi.jpg) 	

The Dashboard should list now all available browsers on this system (if not, go to [No browsers in Dashboard](../docs/troubleshooting-sakuli-client.md#no-browsers-in-dashboard): 

![db_browsers](../docs/pics/w_sahi_dashboard_browsers.jpg) 	
Click on any browser you like; Sahi will start it and present the default start page: 

![sahi_start](../docs/pics/sahi_startpage.jpg) 

If neccessary, define now your company's proxy within Sahi: [Proxy settings](../docs/sakuli-manual.md)

Sahi is now installed completely.