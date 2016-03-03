## Change Log of Sakuli Releases

### Version 1.0.0
* First step tutorial and https documentation. Fixes #161, fixes #53 partially.
* fix #32 highlight function on linux does not work (in underlying SikuliX library)
* close #102 add method `dragAndDropTo` to the `Region` object
* Changed order of properties.
* Improve example_xfce:
    * Replaced calculator screenshot by a small one.
    * add mouse move action to example_xfce
* close #139 remove PDF als download content type, to enable to use the browser PDF viewer
* close #139 add start chrome + firefox maximised (firefox have to hold the file `localstore.rdf` in his profile folder)
* close #168 add reboot hint if user install the package `Windows environement changes` 
* update the installer translation to the recommend one from https://github.com/izpack/izpack/tree/master/izpack-core/src/main/resources/com/izforge/izpack/bin/langpacks/installer
* fix maven snapshot repository path of the `labs.consol.de` maven-repository

- - -

### Version 0.9.3

* Move to a new binary starter for Windows and Linux (sakuli.exe / sakuli), #150:
    * Change syntax from the new starter to:
    
    ```bash
    Usage: sakuli[.exe] COMMAND ARGUMENT [OPTIONS]
    
           sakuli -help
           sakuli -version
           sakuli run <sakuli suite path> [OPTIONS]
           sakuli encrypt <secret> [OPTIONS]
    
    Commands:
           run 	   <sakuli suite path>
           encrypt 	   <secret>
    
    Options:
           -loop	   <seconds>	  Loop this suite, wait n seconds between
                                      executions, 0 means no loops (default: 0)
           -javaHome   <folder>       Java bin dir (overrides PATH)
           -javaOption <java option>  JVM option parameter, e.g. '-agentlib:...'
           -preHook    <programpath>  A program which will be executed before a
                                      suite run (can be added multiple times)
           -postHook   <programpath>  A program which will be executed after a
                                      suite run (can be added multiple times)
           -D 	   <JVM option>   JVM option to set a property at runtime,
                                      overrides file based properties
           -browser    <browser>      Browser for the test execution
                                      (default: Firefox)
           -interface  <interface>    Network interface card name, used by
                                      command 'encrypt' as salt
           -sahiHome   <folder>       Sahi installation folder
           -version                   Version info
           -help                      This help text

    ```
    
    * modify VNC dokumentation  to flag `-preHook` and `postHook`
    * change documentation and docker scripts to new starter syntax `sakuli run TEST_SUITE [OPTION]`
    * add binaries `sakuli` and `sakuli.exe` from repo https://github.com/ConSol/sakuli-go-wrapper
    * remove `sakuli.sh/sakuli.bat`
* Add new forwarder module `Icinga2`, see #145: 
    * Rest client to send the results to Icinga2 API 
    * new property `sakuli.forwarder.gearman.nagios.template.suite.summary.maxLength` to cut to long output due to error messages
    * introduce `sakuli.forwarder.icinga2` properties // consolidate `sakuli.forwarder.database` properties
* Separate examples for Window 7 and Windows 8
* close #118 improved output of nagios messages
* close #151 add a bunch of Windows registry settings to the installer, to improve the test stability.  Disables graphical effects, screen saver and error reporting.
* fix #135 Environment similarity: 
  * Extract constant Environment#DEFAULT_SIMILARITY to `sakuli-default.properties` as `sakuli.environment.similarity.default`:
  * Set default similarity to `0.99`
* close #163: add clean up method, which release all modifier keys before a test case will startet and at the teardown phase
* fix #162: release keys didn't work correctly => update to sikulix version 1.1.998 and add function "run as admin" to dev suites
* add an Around Aspect to `net.sf.sahi.util.Utils.getCommandTokens` to fix parsing errors during calling native commands, see http://community.sahipro.com/forums/discussion/8552/sahi-os-5-0-and-chrome-user-data-dir-containing-spaces-not-working
* Documentation how to solve increasing sahi profile folders. Closes #164.
* reduce wait times for example test suites
* fix `firefox_portable` executable path in `browser.xml`: replace it with $userDir relativ path
* consolidate forwarder properties: adjust `jdbc.*` properties to `sakuli.forwarder.database.*` properties
* improve logging of database receiver
* fix #153 `sakuli.log.maxAge` error, is smaller then 1
* check_sakuli.php: added wrapper for function declarations to fix errors in PNP basket (cannot redefine...)
* cl: update installer with special cl installer preselected options
* close #155: add environment variables to --version output
* fix for #158: linux installer correct firefox var to `MOZ_DISABLE_OOP_PLUGINS`
* Added ff_purge_profile.bat to helper scripts (delete sqlite file before each run)
* close #155: add -version parameter to Sakuli starter (sakuli / sakuli.exe)
* close #153 log data rotation: * add a property sakuli.log.maxAge in days (default 14 days) * deletes all files that are older than the defined days in the folder `sakuli.log.folder`

### Version 0.9.2
* add setting some firefox variables (`MOZ_DISABLE_OOP_PLUGINS`, `MOZ_DISABLE_AUTO_SAFE_MODE`, `MOZ_DISABLE_SAFE_MODE_KEY`) for UI testing to the installer, see #158.
* Executable JAR installer `sakuli-vX.X.X-installer.jar`, downloadable via [](https://labs.consol.de/sakuli/install/), see #24.
  * The installer contains a complete Sakuli setup and the following options:
    ![inst_2](docs/pics/installer_2.png)
    * **1)** will set/update the environment variable `__SAKULI_HOME__` to this version.
    * **2)** will set/update the environment to a recommend UI testing configuration. In examples disables the Firefox safe.
    * **3)** will install one example test suite per OS which will help you to test and understand Sakuli.
    * **4)** will install Firefox Portable, which can be used exclusively for Sakuli Tests.
    * **5)** will install [QRes](http://sourceforge.net/projects/qres/), a open source screen mode changer (Windows only)
  * modify docker images to new headless linux installer
  * custom sahi `browser_types.xml` for firefox, firefox_portable, chrome
* Property `sikuli.typeDelay` now also set the `RobotDesktop.stdAutoDelay` to make the delay more effective, default is `0.0`, #154.
* issue #149 add `Application.kill()` to force closeting an app without "save prompts"
* issue #94: disable highlighting in case of an exception
* docker container: modify test suite permissions after test run in `sakuli_startup.sh`
* Improve typing handling #154:
    * typing all special characters via unicode keyboard shortcuts
    * keyboard mapping only contains alphanumeric characters, so region.type now will work with all local keyboards, because of typing special characters via UFT-8
    * Mac's currently not supports directly typing UFT-8 keys, by default  see https://en.wikipedia.org/wiki/Unicode_input#In_Mac_OS . Unicode typing will only be used if correct keyboard is activated.
* improve takeScreenshot: now also can handle with absolute paths
* rename `Region.takeScreenShot` to `Region.takeScreenshot`
* fix #107: Fix Sikuli `LinuxUtil` Implementation of:
  * Focus application
  * Closing application
  * Make  output and control of native commands more flexible
* include Sahi base installation to java DSL, #24
* modify properties, so that all default values are included
    * add check that `testsuite.id` will be defined at least
    * allow to write test without explicit image folder path, to enable java tests
* added docker-container `sakuli-omd-labs-ubuntu` for a Sakuli preconfigered [OMD](http://omdistro.org/)  
* sakuli.bat: added exitcode, fixes bug #128 (Errors with paths containing spaces)
* fix #142: remove some not valid windows escape chars like `'` or `"` to prevent a InvalidPathException in SakuliStarter arguments
* docker-containers.md: Added hint for boot2docker users.
* check_sakuli.php: fixed #132 (suite runtime)
* close #103: make docker-container able to override the running testsuite in `docker run`  `CMD` arguments  
  * make the `sakuli.sh` command line parameters usable in `docker run`  `CMD` arguments, like for example `docker run consol/sakuli-centos-xfce '--run $SAKULI_TEST_SUITE --browser chrome'`
* Added documentation how to configure HTTPS in Sahi. #53
* Rename README.md to index.md in case of https://readthedocs.org/projects/sakuli/
* headless-linux.md: Added crontab documentation.

### Version 0.9.1
* fix #116 wrong comma in gearman output
* sakuli.bat: added SAKULI_HOME fallback if env var not set #124
* sakuli.bat: added javahome parameter, added JVM option passing #122
* update sikuliX version to 1.1.993
* Merge branch 'dev-v0.4' into dev
* sakuli.sh: JVM options (-D) allowed. #75
* Improve Nagios monitoring integration:
    * check_sakuli.json: added first JSON template for grafana
    * check_sakuli.php: logfile destination now contains hostname and service description
    * check_sakuli.php: removed UNIT var. Everything is in seconds.
    * CheckMySQLHealthSakuli.pm: no perfdata on stale result (fixes #120), small output improvements
    * check_sakuli.php: fixed bug, Suite graph did not have correct value.
    * CheckMySQLHealthSakuli.pm: adjust perfdata output as on gearman output (closes #106)
    * Adapted mysql_purge.sh to new table names, added parameter. #10
* Merge pull request #108 from sgbeal/master
* Add Docker container `consol/sakuli-ubuntu-xfce` and `consol/sakuli-centos-xfce`, see #103:
    * add return value to sakuli_startup.sh and add exit state to sakuli.sh script
    * HTML5-VNC client (noVNC) enabled containers
    * `docker-compose.yml` example for parallel test-execution
    * add example_xfce_test for the docker-containers
* fix PrioritizedServiceComparator so now 2 service with the same priority will also accepted
* close #49 add Environment#runCommand method
* add `takeScreenshot()` method to Region object to get the current region as screenshot
* Merge pull request #99 from c-kr/dev
* close #46 - add read the docs links and badge
* improve the way to include additional image folders, #96:
    * add method `addImagePaths` to the TestCase functions
    * add variable '$testSuiteFolder' as global variable to JavaScript testcase.js for a more strait filepath handling
* add optional parameter 'silent' to Application.close function to suppress exceptions
* add OS identification functions 'isWindows', 'isLinux', 'getOsIdentifier' to Environment class
* close #98 add source and javadoc files to maven build artifacts (on release-builds)
* close #92 exclude Base64 String in log output
* fix #95 state of suite/case/step is always 'OK', if no error occurs and warning + critical time is 0
* close #81 Expanded color array in PHP template and change numbering scheme for cases and steps to 3-digits

### Version 0.9.0
* close #74 extract logging functions to separate javascript class Logger
* close #70 rename sakuli.screenbased.* properties to sikuli.*
* close #42 rename Application#closeApp to Application#close in javascript
* close #27 modify 'non screenshot exception handling' // add TestCaseAction#throwException(message,screenshot)
* add mysql Dockefile for sakuli database forwarder setup, see #10
* close #10 rename table name from sahi to sakuli
* rewritten documentation for sahi delay.
* close #79 rename property `sahi.proxy.requestDelayOnSikuliInput.delayTime` -> `sahi.proxy.onSikuliInput.delayPerKey` and `sahi.proxy.requestDelayOnSikuliInput.refreshTime` -> `sahi.proxy.onSikuliInput.delayBeforeInput`
* finish JavaDSL to be fully supported of all Sakuli features also in Java
* fix #11 custom sahi-port handling (use different ports for sahi-proxy)
* close #7 update sahi-jar verison to sahi 5.0

- - -   

### Version 0.5.0
* rename sakuli.autoHighlight.seconds to sakuli.highlight.seconds
* Documentation
* fix #72 modfy testsuite.suite file writer - just overwrite the file if any blank lines are inside
* add Environment#resetSimilarity()
* fix api generation script
* improve error message for invalid paths in 'testsuite.suite' file
* add support for more screenshot paterns - .jpg, .JPG, .png, .PNG
* .#52 rename sakuli.receiver properties to sakuli.forwarder
* fix #71 add the resumeOnException flag to some missing handleException calls
* refactor exception handling // improve exception handling for javaDSL
* refactor dependency management // extract bin, config, libs to new common 'module'
* .#13 rename screeshot property to 'sakuli.screenshot.onError'
* .#20 enable testCase.endOfStep("name")
* .#66 add -b, --browser into sakuli.jar/sakuli.sh
* .#64 Added Linux (sakuli.sh) and Windows (sakuli.bat) starter.
* .#55 low-level-mouse functions, add mouseMove(), mouseUp(mouseButton), mouseDown(mouseButton)
* .#60 refactor command line options
* .#62 move log-level settings to sakuli.properties
* .#60 introduce a 'sakuli-default.properties' file to move the sakuli.properties to the test suite root
* .#60 introduce new file system structure

 - - -

### Version 0.4.9 (Bugfix Release)
* add #106 add warn/crit thresholds as perfdata values for the Gearman results
    * Adaption for 3-digit case/step ids
    * PNP template with unknown perfdata support
    * added TICKer for incomplete data, warn/crit states
    * Changed color scheme
* add #77 separate error state to identify the affected TestCaseStep on errors:
    * modify SakuliExceptionHandler to find current Step and enable adding exception to the current step
    * add error message output for exceptions in TestCaseSteps
* add #31 determine all not executed TestCaseSteps, to secure that the nagios performance graphs are displayed correctly:
    * introduce new TestCaseStepState INIT
    * modify nagios RRD performance data output for initialized and not started steps to typ 'unknown'
    * add caching mechanism the step information for not started steps implementation
    * call write cached steps information on every 'not error' result
    * gearman forward: write unknown values to every result line if a suite, case or step entity has finished with errors or have even not been called
    * database forwarder: write NULL instead of '0' at warning and critical times
* add `takeScreenshot()` method to Region object to get the current region as screenshot
* add troubleshooting for Nullpointer at `new Application("..").getRegion()` to documentation
* fix PrioritizedServiceComparator so now 2 service with the same priority will also accepted
* add jenkins-build badge
* add #46 add dev-v0.4 read-the-docs & read-the-docs badge
* add #96 add variable '$testSuiteFolder' fore more strait forward import handling
* fix dependency path of javafx for java7
* close #92 exclude Base64 String in log output
* modify documentation of warning / critical times
* add testcase.endOfStep function without warning time
* add #81 change numbering scheme for cases and steps to always three digits to expanded color array in PHP template

### Version 0.4.8
* fix bug: test suite has stat 'OK' instead of 'RUNNING' during the execution
* improve logging for more information, see [Sakuli - Manual](docs/sakuli-manual.md)
* clearify the sakuli encryption functionality - modify documentation and improve the implementation, see #5
* refactor data structur, see #60
* exctract `sakuli.properties` to the test suits folder and introduce a `sakuli-default.properties` file.

### Version 0.4.7
* add function `getLastUrl()` to the `TestCase` functions, to enable URL based test case handling.
* uncomment some receiver properties in `sakuli.properties` to make the property overriding more generic.
* fix bug that `new Region("image_pattern.png").click();` always clicks on the center of the screen
* introduce experimental JAVA-DSL as new module

### Version 0.4.6
* add `sleep()` method to Region
* `keyUp(...)`, `keyDown(...)` and `write(...)` method to the Region and Environment functions to have more control over the typing.

### Version 0.4.5
* add method to set an delay for the sahi-status-requests, so that no key or click events will be lost by the JavaScript engine of the Browser, see new entry in `sakuli.properties`:
    ```
     # Specifies the interval in milliseconds, what should be applied when sikuli based input
     # (like typing or clicking) is interacting with a Browser website.
     # This setting only make sense, if your test does NOT use Sahi functions for controlling the
     # testing website. This setting will prevent the test for losing some key or click events
     # in case of blocking, synchronous sahi-interal state requests.
     #
     #sahi.proxy.requestDelayOnSikuliInput.delayTime=500
     #
     ### refresh time for the sahi proxy to set the delay time
     #sahi.proxy.requestDelayOnSikuliInput.refreshTime
    ```


### Version 0.4.1
* update release build so that the zipped-release files can be downloaded from  [http://labs.consol.de/sakuli/install](http://labs.consol.de/sakuli/install).
* remove zipped-release files from git repository
* documentation update
* build automatic sakuli-api documentation
* clean up repository
* introduce some maven-profiles for individual usage
* change `.inc` and `.sah` file ending to `.js`
* fixed some typos
* set up jenkins build

### Version 0.4.0
* centralized the configuration of properties files:
	* `_include/sakuli.properties` now contains all possible configuration options for Sakuli. These are the _default values_ for all tests
	* `<test-suite>/testsuite.properties` contains the _test suite specific configuration options_. The only mandatory property here is the test suite identifier `testsuite.id`. All other properties are optional.
	* Options set in `testsuite.properties` will override the default settings in `sakuli.properties`
* Proxy configuration options can now be set in `sakuli.properties` (defaults) or  `testsuite.properties` (suite specific)
* Re-organized the folder structure of `sakuli-zipped-release-vX.X.X.zip` and source code directory.
* Extended logging with more configuration possibilities (SLF4J with underlying logback-Logging)
* Consolidation of the applicationContext files
* Remove the program-based setting of system properties.
* The possibility to disable the "encryption interface" with new property `sakuli.encryption.interface.testmode=true`
* Added a separate module for integration testing
* Bugfixing and extended unit tests
* documentation update
* Added a separate module for integration testing.
* Bugfixing and extended unit tests.
* Update the documentation

### Version 0.4.2
* Introducing receiver concept: For each receiver the results will be sent. Currently Supported JDBC-Databases and the Gearman
  receiver.

* Gearman receiver: sent all data directly to your OMD/Nagios distribution. Currently it is missing that the screenshots
  will also be transferred. This will be fixed in the next version

* Bufixing in maven build, exception handling, testcase ids and  added some more unit tests
