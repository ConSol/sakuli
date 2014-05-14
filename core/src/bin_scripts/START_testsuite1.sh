#!/bin/sh
#Starter script for the sakuli application
#
#
#project folder: default def
PROJECT_FOLDER=`dirname $0`/..
echo project-folder: $PROJECT_FOLDER

#define your test suite folder here:
TEST_SUITE_FOLDER=$PROJECT_FOLDER/sakuli_test_suites/example
echo suite-folder: $TEST_SUITE_FOLDER

#internal folders for application logic
INCLUDE_FOLDER=$PROJECT_FOLDER/_include
LIB_FOLDER=$PROJECT_FOLDER/bin/lib
SAKULI_JARS=$LIB_FOLDER/*:$LIB_FOLDER/lib/resource:$INCLUDE_FOLDER/log4j.properties
echo jar-file: $SAKULI_JARS

#start the java application
java -Dsikuli.Home=%LIB_FOLDER% -Dlog4j.configuration=file:$INCLUDE_FOLDER/log4j.properties -classpath $PROJECT_FOLDER/bin/sakuli.jar:$SAKULI_JARS de.consol.sakuli.starter.SakuliStarter -run "$TEST_SUITE_FOLDER" "$INCLUDE_FOLDER"