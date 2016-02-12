#!/bin/bash

if [ -z "$SAKULI_HOME" ]; then
    echo "Environment variable 'SAKULI_HOME' is not set!" \
        "Please ensure that 'SAKULI_HOME' points to the installation folder of your expected Sakuli version!" \
        && exit 6
fi

basedir=`dirname $0`
$SAKULI_HOME/bin/sakuli.sh --run "$basedir/example_ubuntu"
