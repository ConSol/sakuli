@echo off

set TEST_SUITE_FOLDER=%SAKULI_HOME%\sakuli_test_suites\example

set SAKULI_JARS=%SAKULI_HOME%\bin\lib\*;%SAKULI_HOME%\bin\lib\resource;%SAKULI_HOME%\_include\log4j.properties

cscript.exe %SAKULI_HOME%\_include\vb-scripts\killproc.vbs -f %SAKULI_HOME\_include\vb-scripts\procs_to_kill.txt

echo jar-file: %SAKULI_JARS%
java -Dsikuli.Home=%SAKULI_HOME%\bin\lib -Dlog4j.configuration=file:%SAKULI_HOME%\_include\log4j.properties -classpath %SAKULI_HOME%\bin\sakuli.jar;%SAKULI_JARS% de.consol.sakuli.starter.SakuliStarter -run "%TEST_SUITE_FOLDER%" "%SAKULI_HOME%\_include"
