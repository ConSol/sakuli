Installation guide for Sakuli - Developers
======================================

Requirements
==========
* Access to the issue-tracker project (currently JIRA)
* Git
* Development environment (we advise IntelliJ IDEA)
* Java JDK 1.7
  <br>(unfortunately JDK 1.8 is currently not support in case that the aspectj-compiler-plugin won't work with it :-/ )</br>


Sakuli setup
=========
Import
---------
* Check out via git
* Import as maven-project

Database setup
-----------------------
Setup your local database to save your results of the test case executions. The database won't be needed for running `mvn install`.
* __User:__ `sahi`
* __Password:__ `sahi`
* __Database:__ `sahi`
* __SQL-Script:__ [core/src/database_scripts/create_and_drop_database.sql](/core/src/database_scripts/create_and_drop_database.sql)

Install Sahi
------------
* Execute in the folder 'install/3rd-party' the sahi JAR:

    ```
    java -jar install_sahi_v44_20130429.jar
    ```

* As installation path use `<project-dir>/sahi`. Ensure that the path in the property file
  `core/src/main/_include/sahi.properties` points to your Sahi installation folder.

* As installation packs use only __"Sahi Core", "Tools", "Userdata"__



Development-Environment-Konfiguration
-----------------
* Execute 'mvn clean verify' to ensure that all is setup correctly
* Mark `*.inc` and `*.sah` as JavaScript-Files in your IDE
* Include the license header to your IDE
  * For IntelliJ see [Help](http://www.jetbrains.com/idea/webhelp/generating-and-updating-copyright-notice.html) or our predefined copyright configuration under [intellij/copyright](intellij/copyright).
  * License Header:
    ```
    Sakuli - Testing and Monitoring-Tool for Websites and common UIs.

    Copyright 2013 - $today.year the original author or authors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ```
* Build up you own run configuration, to run your sakuli test cases directly from your IDE. For Intellij see our predefined run configurations under [intellij/run-configs](intellij/run-configs)
  * __classpath of module:__ `sakuli-core`
  * __working directory:__ `$MODULE_DIR$`
  * __main class:__ `de.consol.sakuli.starter.SakuliStarter`
  * __program arguments:__ 
    ```-run <path to your sakuli test suite folder> <path to your _include folder> <path to your sahi folder>```
    e.g. for the provided Win7 example use `-run _sakuli_test_suites/example src/main/_include ../sahi`
  * __VM options:__ `-Dlog4j.configuration=file:src/main/resources/log4j.properties`


___Attention!___
--------------
If you run your sakuli test the first time (like the windows example), you might get an native library error.
In that case sakuli didn't find the correct native library to work with.
To solve the problem, just log out and log in again to your workstation, so that the modified PATH-Variable will be read.
