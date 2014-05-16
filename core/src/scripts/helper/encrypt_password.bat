
set SECRET=test
set ENCRYPTION_INTERFACE=eth3

java -classpath sakuli.jar;..\..\bin\lib\* de.consol.sakuli.starter.SakuliStarter -encrypt "%SECRET%" -interface "%ENCRYPTION_INTERFACE%"
