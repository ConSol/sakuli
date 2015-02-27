#!/bin/sh

SUITE=example_linux

INCLUDE_FOLDER=$SAKULI_HOME/_include
SAKULI_JARS=$SAKULI_HOME/bin/lib/*

java -classpath $SAKULI_HOME/bin/sakuli.jar:$SAKULI_JARS org.sakuli.starter.SakuliStarter -run "$SAKULI_HOME/sakuli_test_suites/$SUITE" "$INCLUDE_FOLDER"
