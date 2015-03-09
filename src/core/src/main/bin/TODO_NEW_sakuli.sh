#!/bin/sh

SUITE=/home/tschneck/git-files/sakuli/src/core/_dev_test_suites/_testsuite1_Ubuntu_BB

MAIN_FOLDER=$SAKULI_HOME
SAKULI_JARS=$SAKULI_HOME/libs/java

java -classpath $SAKULI_JARS/sakuli.jar:$SAKULI_JARS/* org.sakuli.starter.SakuliStarter -run "$SUITE" "$MAIN_FOLDER" "$MAIN_FOLDER/../sahi"
