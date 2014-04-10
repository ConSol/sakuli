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
>__User:__ sahi
>__Password:__ sahi
>__Database:__ sahi
>__SQL-Script:__ create_and_drop_database.sql

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
* Follow the [local maven setup instructions](developer_maven_local_repo_instructions.md)
* Execute 'mvn clean test'
* Mark *.inc__ and *.sah as JavaScript-Files in your IDE

___Attention!___
--------------
If you run your sakuli test the first time (like the windows example), you might get an native library error.
In that case sakuli didn't find the correct native library to work with.
To solve the problem, just log out and log in again to your workstation, so that the modified PATH-Variable will be read.
