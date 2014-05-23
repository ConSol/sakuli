## Change Log for Sakuli Releases

- - - 

### Version 0.4.1
* update release build so that the zipped-release files, will be provided over the site [http://labs.consol.de/sakuli/install](http://labs.consol.de/sakuli/install).

* remove zipped-release files from git repository

* update documentation

* build automatic sakuli-api doc

* clean up repository

* introduce some maven-profiles for individual usage
  
* change `.inc` and `.sah` file ending to `.js`

* fix some typos

* set up jenkins build
  

### Version 0.4.0
* Centralise the configuration of properties files to:
 * `_include/sakuli.properties` contains all possible configuration option for Sakuli. The configured value will be used
 as default over all test executions
 * `<path-to-test-suites>/testsuite.properties` contains the test suite specific configuration. The only mandatory
  property there is the test suite identifier `testsuite.id`. All other properties are optional. To configure the
  test suite individual, there is the possibility to override all properties from the `sakuli.properties` file.
 
* Add new configuration possibilities for external company proxies. It is now possible to config the company proxy
inside of the `sakuli.properties` or the `testsuite.properties`

* Improved the folder structure of `sakuli-zipped-release-vX.X.X.zip` and of the source code directory.
 
* Extended logging with more configuration possibilities (SLF4J with underlying logback-Logging).

* Consolidation of the applicationContext files

* Remove the program-based setting of system properties. 

* The possibility to disable the "encryption interface" with new property `sakuli.encryption.interface.testmode=true` 

* Added a separate module for integration testing.

* Bugfixing and extended unit tests.
  
* Update the documentation
