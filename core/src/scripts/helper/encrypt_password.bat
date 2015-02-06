
set SECRET=%1
set ENCRYPTION_INTERFACE=%2

java -classpath sakuli.jar;..\..\bin\lib\* org.sakuli.starter.SakuliStarter -encrypt "%SECRET%" -interface "%ENCRYPTION_INTERFACE%"
