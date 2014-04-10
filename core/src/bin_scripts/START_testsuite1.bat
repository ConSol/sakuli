::Starter script for the sakuli application
::
::
::project folder: default def
PROJECT_FOLDER=%0
echo project-folder: %PROJECT_FOLDER%

::define your test suite folder here:
TEST_SUITE_FOLDER=%PROJECT_FOLDER%\_sakuli_test_suites\_test_xp_on_barbone
echo suite-folder: %TEST_SUITE_FOLDER%

::internal folders for application logic
INCLUDE_FOLDER=%PROJECT_FOLDER%\_include
LIB_FOLDER=%PROJECT_FOLDER%\bin\lib
SAKULI_JARS=%LIB_FOLDER%\*;%LIB_FOLDER%\lib\resource;%INCLUDE_FOLDER%\log4j.properties

::start the java application
echo jar-file: %SAKULI_JARS%
java -Dsikuli.Home=%LIB_FOLDER% -Dlog4j.configuration=file:%INCLUDE_FOLDER%\log4j.properties -classpath %PROJECT_FOLDER%\bin\sakuli.jar;%SAKULI_JARS% de.consol.sakuli.starter.SakuliStarter -run "%TEST_SUITE_FOLDER%" "%INCLUDE_FOLDER%"
