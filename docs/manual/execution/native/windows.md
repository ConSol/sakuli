TODO extracted from installation-client.md

# How to install Sakuli natively on Windows

This page describes the steps to **install and test** Sakuli on **Windows**.
If you want to **update** Sakuli, see the [upgrade process](upgrade-process.md) documentation.

The default installation path of a particular Sakuli version is referenced as `%SAKULI_HOME%` (also called the "*version*" folder) which is inside of the folder "sakuli":

    > echo %SAKULI_HOME%
      C:\Program Files (x86)\sakuli\sakuli-v1.1.0-SNAPSHOT

Values surrounded by double underscores are have to be filled individually by you (e.g. `__IP_ADDRESS__`).

## Prerequisites

You can run Sakuli on physical as well as on virtual Linux machines.

FIXME: Bitarchitektur?

* **OS**: **Microsoft Windows** (version 7 and higher)
* **RAM**: 2GB or more
* **CPU**: at least two CPU cores
* **Software:**
  * Java JRE >= 1.8

## Recommendations

* Set the desktop background to a homogenous color.
* disable any screen locking mechanisms
* Run Sakuli with a dedicated user
* Sakuli needs a reliable and predictable desktop environment: make sure that there are no pop-up windows of services or applications
* If Sakuli is running within a VM, change the desktop resolution to a fixed value (e.g. 1024x768) and disable any auto-resizing mechanisms of the guest display. This ensures that the guest's resolution does not change in case that you resize its window.
* Other optional steps see ["Desktop tuning"](./troubleshooting-tuning-sakuli-client.md#desktop-tuning).
* the client should not have more than one physical screen

## Sakuli installation

* Download the **Sakuli Installer** from  [http://labs.consol.de/sakuli/install](http://labs.consol.de/sakuli/install)
  * current **development** snapshot = `sakuli-vX.X.X-SNAPSHOT-installer.jar`
  * current **stable** version = `sakuli-vX.X.X-installer.jar` (recommended)

Double-click on the downloaded .jar file (Windows) to start the installer:

![inst_1](./pics/installer_1.png)

After you have accepted the licence and choose the installation folder, select the packages to install:

![inst_2](./pics/installer_2.png)

* **1)** will set/update the environment variable `%SAKULI_HOME%` to the new version.
* **2)** will set/update environment settings which have proved as best practice for UI tests.
* **3)** will install one example test suite per OS which will help you to test and understand Sakuli.
* **4)** will install Firefox Portable, which can be used exclusively for Sakuli Tests.
* **5)** will install [QRes](http://sourceforge.net/projects/qres/), a open source screen mode changer (Windows only)

After the files have been copied, the installer updates the current user's environment.

If you have installed Sakuli on Windows, reboot now to take the registry changes effect (e.g. disabled Cleartype fonts).

In the end you are offered to generate a automatic installation script to install Sakuli headless:

![inst_3](./pics/installer_3.png)

A headless installation (e.g. when building Docker images) can be executed with:

    java -jar sakuli-vX.X.X-installer.jar auto-config.xml

#### Test Sahi

Now is the right time to test if Sahi can start a Sahi-controlled browser. Execute `%SAKULI_HOME%/sahi/userdata/bin/start_dashboard.(sh|bat)` to open the **Sahi Dashboard**. It should now list all available browsers on this system (on Windows: also Firefox Portable).

![dashboards](./pics/inst_dashboards.jpg)

Click on any browser icon; it should start and present you the start page of Sahi:

![sahi_start](../docs/pics/sahi_startpage.jpg)

At last, test the **Sahi Controller**:
* on **Windows** press the ALT key
* on **Linux** press the ALT+CTRL key

and double-click on any white space. If you are getting a new window showing the "Sahi Controller", you're done. Close all browser windows and Sahi.

![controller_linux](./pics/installer_4_l.png)

![controller_windows](./pics/installer_4_w.png)

**Additional documentation:**

* If you are sitting behing a company proxy, refer to [Proxy settings](./sakuli-additional-settings.md#sahi-behind-a-proxy).
* Refer to [Browser configuration](./additional-settings.md#browser-configuration)) for instructions how to register other browsers.

### Additional software

#### additional packages on Linux
(required)

Install the following packages if you are on Linux:

* Ubuntu: `sudo apt-get install tesseract-ocr wmctrl xdotool`
* openSUSE `sudo zypper install tesseract wmctrl xdotool`


#### PhantomJS
(optional)

Currently, *each* Sakuli test requires to start a browser, which is not very handy for pure Sikuli GUI tests (=where no browser at all is needed). For that case, use a headless browser like [PhantomJS](http://phantomjs.org). Refer to [Browser configuration](./additional-settings.md#browser-configuration) for more information.

Attention: PhantomJS 2 is currently unsupported. Use version 1.9.x


#### Screenshot tool
(optional)

Use a screenshot tool which is able to

- capture areas of the screen
- delay the creation of screenshots for x seconds (important if Sikuli must navigate through menues)

A good choice is

* [Greenshot](http://www.getgreenshot.org) on **Windows**  
* [Shutter](http://shutter-project.org/) on **Linux**.

Always make sure that screenshots are saved without compression. Sikuli uses a default similarity of 0.99, which internally means that "more than 99%" => 100% pixels must conincide. Decreasing similarity should only be neccessary if the pattern images are of poor quality or the region compared to always slightly differs from the pattern image.


#### Editor
(optional)

You're doing better if you do *not* use gEdit or Windows Notepad to edit Sakuli files. It is recommended to use an Editor with JavaScript support, e. g.:

* on **Windows** install for instance [Notepad++](http://notepad-plus-plus.org/)
* on **Linux** use [Atom](https://atom.io)

It also possible to use professional programming IDEs like [IntelliJ](https://www.jetbrains.com/idea/), [Netbeans](https://netbeans.org/) or [Eclipse](https://eclipse.org).


## Test

You are now ready to run the **first minimal Sakuli test** to see if Sakuli and its components are working well together. Open a new terminal to start a test:

* **Ubuntu**: `sakuli run __INST_DIR__/example_test_suites/example_ubuntu/`
* **openSUSE**: `sakuli run __INST_DIR__/example_test_suites/example_opensuse/`
* **Windows 7**: `sakuli run __INST_DIR__\example_test_suites\example_windows7\`
* **Windows 8**: `sakuli run __INST_DIR__\example_test_suites\example_windows8\`

Sakuli should now

1.  open **Firefox** with the Sakuli welcome page, highlight some page elements
2.  open the **calculator** and calculate *525+100=625*
3.  open an **editor** and write a **status message**

![](pics/u_vnc_test.png)

**Congratulations - you have installed Sakuli!**

# Next steps

What next? Well, it depends...

* Read our [first-steps tutorial](first-steps.md) and learn to handle Sakuli
* Integrate Sakuli results in monitoring systems:
  * [Open Monitoring Distribution (OMD)](installation-omd.md).
  * [Icinga2](forwarder-icinga2.md)
* Sakuli can also be integrated in **continuous integration environments like Jenkins**. Documentation is coming in the next time (...have one?)
