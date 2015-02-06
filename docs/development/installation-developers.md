# Installation guide for Sakuli - Developers

##Requirements
For the following guide you need

* Access to the issue-tracker tool (currently JIRA)
* Git
* Development environment (we advise IntelliJ IDEA)
* Maven 3 

  __attention for Ubuntu users!!!__ - don't use the default maven version over `apt-get install mvn`
* Java JDK 1.7
  * Please use the original Oracle JDK - OpenJDK unfortunately won't work for the JavaFX based integration test, see [Java FX installation](java_fx_installation). 
  * Ensure that your `JAVA_HOME` system variable links to the correct jdk-version
  * (JDK 1.8 is currently not supported in case that the aspectj-compiler-plugin won't work with it :-/ )


##Sakuli Setup
###Import
* Check out the project via git
* Import the project as a maven-project

###Maven Settings
* Ensure that you have at least installed maven 3, run `mvn --version`
* Config the local maven settings `~/.m2/settings.xml` for your environment as follows:
    
     ```
     <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                           http://maven.apache.org/xsd/settings-1.0.0.xsd">
     <proxies>
       <proxy>
           <id>proxy</id>
           <active>true</active>
           <protocol>http</protocol>
           <host>proxy.your-company.com</host>
           <port>8080</port>
           <nonProxyHosts>localhost</nonProxyHosts>
       </proxy>
     </proxies>
     
     <server>
         <id>labs-consol-sakuli-install</id>
         <username>sakuli</username>
         <privateKey>${user.home}/.ssh/your-key-file</privateKey>
     </server>
 
     <server>
         <id>labs-consol-sakuli-repository</id>
         <username>maven-repository</username>
         <privateKey>${user.home}/.ssh/your-key-file</privateKey>
     </server>
     
     </settings>
     
     ```

###Install Sahi
* Change into the folder 'install/3rd-party' and execute the sahi JAR:

    ```
    java -jar install_sahi_v44_20130429.jar
    ```

* Install Sahi into `<project-dir>/sahi`. Ensure that this path is set in `sahi.proxy.homePath` in file
  `core/src/main/_include/sakuli.properties`.

* You only need to install the components
	* Sahi Core
	* Tools
	* Userdata

###Database Setup (optional, onyl for DB receiver necessary)
Setup a local MySQL database to save the results of test case executions. The database won't be needed for running `mvn install`.

* __User:__ `sahi`
* __Password:__ `sahi`
* __Database:__ `sahi`
* __SQL-Script:__ [core/src/database_scripts/create_and_drop_database.sql](/core/src/database_scripts/create_and_drop_database.sql)


###Development-Environment-Konfiguration
* Execute `mvn clean verify` to ensure that the setup is correct
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
* In order to run Sakuli test cases directly from the IDE, you need to build up a run configuration. For IntelliJ see the predefined run configurations under [intellij/runConfigurations](intellij/runConfigurations)
  * __classpath of module:__ `sakuli-core`
  * __working directory:__ `$MODULE_DIR$`
  * __main class:__ `org.sakuli.starter.SakuliStarter`
  * __program arguments:__ 
    ```-run <path to your Sakuli test suite folder> <path to your _include folder> <path to your sahi folder>```
    e.g. for the provided Win7 example use `-run ../sakuli_test_suites/example src/main/_include ../sahi`


###Important note
If you run your Sakuli test the first time, you might get a native library error, caused by Sikuli, saying that it could not find the correct native library to work with. At the same time, Sikuli already tried to fix the problem by modyfing PATH.  
Just log out and in again, so that the modified PATH-Variable will be read. Sakuli should start now without any error. 
