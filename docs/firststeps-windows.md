# First steps on Windows 7

For this chapter, it is required that you have done the steps in 

[OMD Preparation](installation-omd.md) - How to configure [OMD](http://www.omdistro.org) (containing **Nagios/Icinga/Shinken**) to monitor Sakuli

and 

[Installation (Windows 7)](installation-windows.md) - Hot to set up a Windows Sakuli Client on Windows 7



## Test 

### Windows 
Now it's time to test Sakuli and see how Sahi and Sikuli are working together - therefore we prepared a simple test case which shows the features of Sakuli and tests all functionalities.  

These are the nuts and bolts of the files which belong to a test definition: 

* Change to _**%SAKULI_HOME%**\bin\**_ and open *START_examle_demo_win7.bat* in Notepad. This is the batch file which will start the test case. You can name this file as you like.  

		@echo off
		set TEST_SUITE_FOLDER=%SAKULI_HOME%\sakuli_test_suites\example
												               ^^^^^^^
		set SAKULI_JARS=%SAKULI_HOME%\bin\lib\*;%SAKULI_HOME%\bin\lib\resource;%SAKULI_HOME%\_include\log4j.properties

		cscript.exe %SAKULI_HOME%\_include\vb-scripts\killproc.vbs -f %SAKULI_HOME\_include\vb-scripts\procs_to_kill.txt
		echo jar-file: %SAKULI_JARS%
		java -Dsikuli.Home=%SAKULI_HOME%\bin\lib -Dlog4j.configuration=file:%SAKULI_HOME%\_include\log4j.properties -classpath %SAKULI_HOME%\bin\sakuli.jar;%SAKULI_JARS% de.consol.sakuli.starter.SakuliStarter -run "%TEST_SUITE_FOLDER%" "%SAKULI_HOME%\_include"

	* The line with ^^^^^^^ signs below it tells Sakuli the **test suite folder** - the place where it should find the test definition.
	* The line starting with "cscript.exe" calls _killproc.vbs_ to kill all orphaned processes from previous tests (see [Killing orphaned processes](./sakuli-manual.md))
	* Close _**%SAKULI_HOME%**\bin\START_examle_demo_win7.bat_ and double-Click to start it.  
