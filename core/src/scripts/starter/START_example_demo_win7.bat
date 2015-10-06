@echo off

set SUITE=example

REM START /min ..\helper\record.bat %SUITE% 120

set TEST_SUITE_FOLDER=%SAKULI_HOME%\sakuli_test_suites\%SUITE%

set SAKULI_JARS=%SAKULI_HOME%\bin\lib\*;%SAKULI_HOME%\bin\lib\resource;

cscript.exe %SAKULI_HOME%\scripts\helper\killproc.vbs -f %SAKULI_HOME%\scripts\helper\procs_to_kill.txt

echo jar-file: %SAKULI_JARS%
java -Dsikuli.Home=%SAKULI_HOME%\bin\lib -classpath %SAKULI_HOME%\bin\sakuli.jar;%SAKULI_JARS% org.sakuli.starter.SakuliStarter -run "%TEST_SUITE_FOLDER%" "%SAKULI_HOME%\_include"

exit
