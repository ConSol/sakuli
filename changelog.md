## Change Log of Sakuli Releases

- - -

### Version 0.4.8
* fix bug: test suite has stat 'OK' instead of 'RUNNING' during the execution
* improve logging for more information, see [Sakuli - Manual](docs/sakuli-manual.md)
* clearify the sakuli encryption functionality - modify documentation and improve the implementation, see #5

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
