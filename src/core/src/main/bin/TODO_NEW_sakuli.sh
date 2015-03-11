#!/bin/sh

SUITE=/home/tschneck/git-files/sakuli/src/core/_dev_test_suites/_testsuite1_Ubuntu_BB

SAKULI_JARS=$SAKULI_HOME/libs/java

#sakuli.sh -run ../testsuite -sahi_home ../../sahi -sakuli_home ../SAKULI_APP/sakuli/sakuli-v0.4.8-new-structure-SNAPSHOT
java -classpath $SAKULI_JARS/sakuli.jar:$SAKULI_JARS/* org.sakuli.starter.SakuliStarter -run "$SUITE" -sakuli_home "$SAKULI_HOME" -sahi_home "$MAIN_FOLDER/../sahi"
