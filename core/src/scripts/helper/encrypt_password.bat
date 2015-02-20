@ECHO off
set SECRET="-encrypt %1"
if not "%2" == "" (
    set INTERFACE="-interface %2"
) else (
    set INTERFACE=
)

set SAKULI_JARS=%SAKULI_HOME%\bin\lib\*;%SAKULI_HOME%\bin\lib\resource;

java -classpath %SAKULI_HOME%\bin\sakuli.jar;%SAKULI_JARS% org.sakuli.starter.SakuliStarter "%SECRET%" "%INTERFACE%"

if "%2" == "" echo (interface determined by auto-detection)