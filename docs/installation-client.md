# Sakuli client installation 

This page describes the common steps to **install and test** Sakuli on **Windows** and **Ubuntu**. 

The default installation path of Sakuli is referenced as `%SAKULI_HOME%`, that is 

- `%SAKULI_HOME%` on **Windows** (=`C:\sakuli`)
- `$SAKULI_HOME` on **Ubuntu** (=`/home/sakuli`)

**Conventions**: 

- Wherever you see `%SAKULI_HOME%` in the instructions, you can replace it by the appropriate variable `$SAKULI_HOME` (**Linux**) respectively `%SAKULI_HOME%` (**Windows**).
- Values surrounded by underscores (e.g. `__IP_ADDRESS__`) must be replaced with specific values.
- Unless stated otherwise, slashes/backslashes have to be used depending on the operating system.

Examples (on **Linux**): 

- `__SAKULI_HOME__/sahi/userdata/` => type `/home/sakuli/sahi/userdata/`
- `%SAKULI_HOME%/sahi/userdata/` =>  type `$SAKULI_HOME/sahi/userdata/`

## Prerequisites

We recommend to run Sakuli clients on virtual machines.

* **OS**: **Windows** or **Ubuntu** 14.04 LTS Desktop
* **RAM**: 3GB+  
* if virtualized: **Hypervisor guest additions** for best performance 
* no local firewall running
* user account "sakuli" with **sudo/admin rights**
* desktop appearance 
  * Set the desktop background to a homogenous color. 
  * If you do not want to run Sakuli headless, disable screen locking mechanisms
  * Sakuli needs a predictable desktop environment: make sure that there are no pop-up windows of services or applications. 
  * Other optional steps see ["Desktop tuning")](./troubleshooting-tuning-sakuli-client.md#desktop-tuning).
* the client should not have more than one screen 

## Software installation 
### Java

Sakuli versions including v0.4.* need **Java 7**; all newer versions are based on **Java 8**.  

Installation is straightforward for **Windows**; for **Ubuntu**, see Ubuntuusers Wiki for [Java 7 JRE](http://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_7#Java-7-JRE) / [Java 8 JRE](http://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_8#Java-8-JRE).

Additionally, on **Windows**: 

* From desktop, right-click *My Computer* and click *Properties*
* In *System Properties*, click on *Advanced*
* Edit the **system variable** **%PATH%** and add one of the following paths to the java binary: 
  * Name: `PATH`
  * Value: 
    * `;C:\Program Files\Java\jre7/8\bin`
    * or 
	* `;C:\Program Files (x86)\Java\jre7/8\bin`

### Sakuli 

* Download **Sakuli** from  [http://labs.consol.de/sakuli/install](http://labs.consol.de/sakuli/install)
  * current **development** snapshot = `sakuli-zipped-release-vX.X.X-SNAPSHOT.zip` 
  * current **stable** version = highest version of `sakuli-zipped-release-vX.X.X.zip` 
* Unzip the downloaded archive:
  * on **Windows** into `C:\` 
  * on **Ubuntu** into `/home/sakuli/` (will result in `/home/sakuli/sakuli`)
  
Set `SAKULI_HOME`: 

* **Windows**
  * From desktop, right-click *My Computer* and click *Properties*
  * In *System Properties*, click on *Advanced*
	* Create a new **user variable**: 
      * Name: `SAKULI_HOME`
	  * Value: `C:\sakuli`
	* Create a new **user variable**: 
		* Name: `PATH`
		* Value: `%SAKULI_HOME%\bin\lib\libs`
* **Ubuntu**
  * Add to `/home/sakuli/.bashrc`: 
    * `export SAKULI_HOME=/home/sakuli/sakuli`
  

Additionally, on **Ubuntu** you have to install tesseract and wmctrl manually: 

	sudo apt-get install tesseract-ocr
	sudo apt-get install wmctrl

### Sahi

* Download **Sahi v44** from [http://sourceforge.net/projects/sahi/files/?source=navbar](http://sourceforge.net/projects/sahi/files/) (Version 5 will be supported soon)
* Unpack the downloaded file and start the installation by executing `java -jar __DOWNLOADED_JAR_FILE__`, respectively by double clicking this file on **Windows**. In the installation assistent, set: 
	* Installation path: `__SAKULI_HOME__/sahi`
	* select all packages to install

Sahi can be tested now for the first time. Open *"Start Sahi"* from *"Applications - Programming"* on **Ubuntu**, or by double clicking the icon on the **Windows** desktop. 

<!--- fixme anker, titel -->

The **Sahi Dashboard** should now list all available browsers on this system. To use the minimal test cases (see below), we recommend to install Firefox, if not yet done. (see [Adding browsers to Sahi](../docs/additional-settings.md#adding-browsers-to-sahi)): 

![db_browsers](./pics/w_sahi_dashboard_browsers.jpg) 
![db_browsers](./pics/u_sahi_dashboard_browsers.png) 
	
After clickint on a browser icon you should see the default start page of Sahi: 

![sahi_start](../docs/pics/sahi_startpage.jpg) 

If neccessary, set the proxy Sahi is behind of: [Proxy settings](../docs/sakuli-additional-settings.md#sahi-behind-a-proxy)

### Additional software

All components below are optional but recommended: 

#### PhantomJS
	
<!--- anker  fixme -->	
Currently, *each* Sakuli test requires to start a browser, which is not very handy for pure Sikuli GUI tests (=where no browser at all is needed). For that case, use a headless browser like [PhantomJS](http://phantomjs.org). Refer to [Adding browsers to Sahi](../docs/sakuli-additional-settings.md) for more information. 


#### Screenshot tool 
 
You should use a screenshot tool which is able to

- capture areas of the screen
- delay the creation of screenshots for x seconds (important if Sikuli must navigate through menues)

A good choice is

* [Greenshot](http://www.getgreenshot.org) on **Windows**  
* [Shutter](http://shutter-project.org/) on **Ubuntu**.

#### Notepad++
You're doing better if you do *not* use gEdit or Windows Notepad to edit Sakuli files. 

* on **Windows** install for instance [Notepad++](http://notepad-plus-plus.org/) 
* on **Ubuntu** use [Bluefish](http://bluefish.openoffice.nl/index.html)
	
## Test

You are now ready to run the **first minimal Sakuli check** to see if Sakuli and its components are working well together. 

* On the **Ubuntu** desktop, open a terminal window and execute `~$ sakuli/scripts/starter/START_example_linux.sh` 
* in **Windows**, doubleclick on `C:\sakuli\scripts\starter\START_example_windows.sh`

On both platforms you should see that Sakuli

1.  opens **Firefox**
2.  opens the **calculator** and calculates *525+100=625* 
3.  opens an **editor** and writes a **status message**

![](pics/u_vnc_test.png)
