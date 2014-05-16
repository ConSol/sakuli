# Installation guide for Sakuli under Windows 7
You probably came from the page "Introduction" - if not, and if you are nor sure what Sakuli is, please read first [README](./README.md). 

## Prerequisites
The following installation manual assumes that...

* you have a fresh installed Windows 7 (no matter if Home/Professional/whatever) machine in front of you. 
* all OS updates are installed
* this machine has access to the internet
* the lcoal firewall is disabled
* there is a user account "sakuli" with admin rights
* you have finished the [OMD Preparation](../docs/installation-omd.md) instructions

We recommend to run Sakuli clients on virtual machines, as they are easy to manage. 

## Preparations
Before you start with the implementation of Sakuli tests, the following settings have to be done on the operating system.
### Disable desktop background 
Set the desktop background to a homogenous color. 

### Disable screen saver and screen locking

Disable everything which can cause the screen to get locked / changed in its appearance.  

### Change theme and title bar colors
Windows 7 comes by default with an "aero" theme, which is quite awkward for Sakuli, because there are many transparency effects which cause window elements to change their appearance dependend on the elements below. For that, change the theme to "Windows Classic".
![classic](.././docs/pics/w_classictheme.jpg)


Furthermore, change the colors of **active** and **inactive** title bars to **non gradient**: 
![titlebars](.././docs/pics/w_titlebar.jpg)

### Enable Window Activation ###
Windows does not allow per default to bring an application in the foreground. This must be allowed for Sakuli: 

* Start -> "regedit"
* [ HKEY_CURRENT_USER\Control Panel\Desktop ]
* "ForegroundLockTimeout" (DWORD) => "0" (default = 30d40xh)
### Disable Window Animation ###
Disable the animation of window minimize/maximize actions: 

* "regedit"
* [HKEY_CURRENT_USER\Control Panel\Desktop\WindowMetrics ]
* "MinAnimate" (String) => "0" 
### Disable Cleartype ###
ClearType ("antialiasing" / "Font Smoothing"), is a technology that is used to display computer fonts with clear and with smooth edges. The MS Terminal Services Client (RDP client) enables this feature depending on the available bandwidth, which means that screenshots made within RDP sessions may be taken without ClearType, but during the test execution on the local console, they are compared with the desktop displayed in ClearType. Although we only had problems with RDP and Cleartype, it is a good idea to disable ClearType completely:

* "regedit"
* [ HKEY_CURRENT_USER\Control Panel\Desktop ]
* "FontSmoothingType" (DWORD) => "0" 

### Disable all visual effects ###
* Start -> Control Panel -> System -> Advanced
* Performance -> Settings -> Visual Effects -> Custom
* Disable everything: 

![visualeffects](../docs/pics/w_visualeffects.jpg)

#### RDP related settings ####
The following steps have only to be done if you are accessing the Sakuli Client with RDP. 
##### Disable Clipboard Sharing #####
The "paste" function of Sakuli uses the clipboard at runtime to decrypt and paste passwords. For this reason, the clipboard exchange of the Sakuli client and the RDP client should be suppressed in the settings tab of your **local Remote Desktop client**:

![clipboard](../docs/pics/w_clipboard.jpg)


This can be set globally in the registry **of your local host**: 

* "regedit"
* [ HKEY_CURRENT_USER\Software\Microsoft\Terminal Server Client ]
* "DisableDriveRedirection" (DWORD) => "1" 

##### Disable the "GUI-less" mode #####
If you minimize the Remote Desktop window (the window that display the remote computer’s desktop), the operating system switches the remote session to a "GUI-less mode" which does not transfer any window data anymore. As a result, Sakuli is unable to interact with the tested application’s GUI, as the whole screen is not visible.

To disable the "GUI-less" mode **on your local host**: 

* "regedit"
* [ HKEY_CURRENT_USER\Software\Microsoft\Terminal Server Client ]
* "RemoteDesktop_SuppressWhenMinimized" (DWORD) => "2"

#### 


## Installation of Sakuli
This chapter includes the installation of Sakuli (which already has Sikuli on board) as well as the installation of Sahi. 
### Java JRE
* Install Java7 JRE. (Make sure that you do **not install ASK toolbar**, which is enabled by default!)
* Modify PATH: 
	* From the desktop, right-click *My Computer* and click *Properties*
	* In *System Properties*, click on *Advanced*
	* Highlight *Path* in the Systems Variable section and click *Edit*. Add the following line to the very end, apply and reboot: 
	
            %ProgramFiles(x86)%\Java\jre7\bin
	

### Sakuli - Install the sakuli-zipped-release
* Download [sakuli-zipped-release-v0.4.0.zip](https://raw.github.com/ConSol/sakuli/master/install/sakuli-zipped-release-v0.4.0-SNAPSHOT.zip)
* Unzip ths downloaded archive to *C:\\* as destination folder (=it will decompress into *C:\sakuli*. 
* From the desktop, right-click *My Computer* and click *Properties*
	* In *System Properties*, click on *Advanced*
	* Create a new user variable **%SAKULI_HOME%**: 
		* Name: **SAKULI_HOME**
		* Value: **C:\sakuli**
	* Create a new user variable **%PATH%**: 
		* Name: **PATH**
		* Value: **%SAKULI_HOME%\bin\lib\libs**
* Reboot the machine


**Alternative download:** Create a local Sakuli repository by cloning from [https://github.com/ConSol/sakuli/](https://github.com/ConSol/sakuli/).

## Configuration

Some configuration settings:

_**%SAKULI_HOME%**_\\__include\sakuli.properties_: 

(jump to [encryption of secrets](../docs/sakuli-manual.md) and come back here)

* *sakuli.encryption.interface=eth3*
* *sahiproxy.homepath=c:/sakuli/sahi* 
* set your database connection information like:
	* *jdbc.port=3307*
	* *jdbc.database=sahi*
	* *jdbc.host=[IPofOMD]*
	* *jdbc.user=sahi*
	* *jdbc.pw=sahi*
	* *jdbc.model=sahi*

### Sahi

* Download the latest version of Sahi from [http://sourceforge.net/projects/sahi/files/latest/download?source=files](http://sourceforge.net/projects/sahi/files/latest/download?source=files)
* Unpack the downloaded file and start the installation by double clicking on "install_sahi_v44_20130429.jar". 
	* Installation path: _**%SAKULI_HOME%**\sahi_
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

### PhantomJS
	
Currently, each Sakuli test will start a browser, even for pure Sikuli GUI tests (=where you don't need any browser). In that case, the headless browser *phantomJS* does the trick. 

* Download the latest version of phantomJS from [http://phantomjs.org](http://phantomjs.org)
* Open the ZIP file and copy *phantomjs.exe* to _**%SAKULI_HOME%**\phantomjs_ (create that folder)
* Save [sahi.js](https://github.com/ConSol/sakuli/blob/master/install/3rd-party/phantom/sahi.js) into _**%SAKULI_HOME%**\phantomjs_

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
	
