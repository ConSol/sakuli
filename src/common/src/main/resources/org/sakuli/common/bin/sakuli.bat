@echo off

set SAKULI_JARS=%SAKULI_HOME%\libs\java

echo Generic Sakuli test starter.
echo 2015 - The Sakuli team.
java -classpath %SAKULI_JARS%\sakuli.jar;%SAKULI_JARS%\* org.sakuli.starter.SakuliStarter %*