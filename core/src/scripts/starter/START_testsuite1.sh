#!/bin/sh
#Starter script for the sakuli application
#
#
#project folder: default def
PROJECT_FOLDER=$SAKULI_HOME
echo project-folder: $PROJECT_FOLDER

#define your test suite folder here:
TEST_SUITE_FOLDER=$PROJECT_FOLDER/sakuli_test_suites/example
echo suite-folder: $TEST_SUITE_FOLDER

#internal folders for application logic
INCLUDE_FOLDER=$PROJECT_FOLDER/_include
SAKULI_JARS=$PROJECT_FOLDER/bin/lib/*
echo jar-file: $SAKULI_JARS

#start the java application
java -classpath $PROJECT_FOLDER/bin/sakuli.jar:$SAKULI_JARS de.consol.sakuli.starter.SakuliStarter -run "$TEST_SUITE_FOLDER" "$INCLUDE_FOLDER"
