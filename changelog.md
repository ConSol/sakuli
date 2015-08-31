## Change Log of Sakuli Releases

### Version 1.0.0

- - -

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
* add #96 add variable '$testSuiteFolder' fore more strait forward import handling // check TODO
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
