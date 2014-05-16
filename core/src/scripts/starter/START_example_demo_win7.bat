@echo off

set TEST_SUITE_FOLDER=%SAKULI_HOME%\sakuli_test_suites\example

set SAKULI_JARS=%SAKULI_HOME%\bin\lib\*;%SAKULI_HOME%\bin\lib\resource;

cscript.exe %SAKULI_HOME%\scripts\helper\vb_scripts\killproc.vbs -f %SAKULI_HOME\scripts\helper\vb_scripts\procs_to_kill.txt

echo jar-file: %SAKULI_JARS%
java -Dsikuli.Home=%SAKULI_HOME%\bin\lib -classpath %SAKULI_HOME%\bin\sakuli.jar;%SAKULI_JARS% de.consol.sakuli.starter.SakuliStarter -run "%TEST_SUITE_FOLDER%" "%SAKULI_HOME%\_include"
