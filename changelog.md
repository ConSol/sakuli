## Change Log of Sakuli Releases

- - - 

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
<!--unklar-->
* The possibility to disable the "encryption interface" with new property `sakuli.encryption.interface.testmode=true` 
* Added a separate module for integration testing
* Bugfixing and extended unit tests
* documentation update
