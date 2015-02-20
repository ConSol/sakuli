#!/bin/sh
# script for encrypt a password
secret="-encrypt $1"
if [ -n "$2" ]; then
    interface="-interface $2"
fi

SAKULI_JARS=$SAKULI_HOME/bin/lib/*

java -classpath $SAKULI_HOME/bin/sakuli.jar:$SAKULI_JARS org.sakuli.starter.SakuliStarter $secret $interface

if [ ! -n "$2" ]; then
    echo "(interface determined by auto-detection)\n"
fi