secret=test
interface=eth0
java -classpath sakuli.jar;lib\* de.consol.sakuli.starter.SakuliStarter -encrypt %secret% -interface %interface%