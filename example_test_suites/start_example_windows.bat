
@echo off

set SAKULI_JARS="%SAKULI_HOME%\libs\java"

set JAVAEXEC="C:\Program Files (x86)\Java\jre1.8.0_40\bin\java.exe"

:begin

java.exe -classpath "%SAKULI_HOME%\libs\java\sakuli.jar;%SAKULI_HOME%\libs\java\*" org.sakuli.starter.SakuliStarter -b firefox_portable -r "%SAKULI_HOME%\..\example_test_suites\example_windows"

goto begin
