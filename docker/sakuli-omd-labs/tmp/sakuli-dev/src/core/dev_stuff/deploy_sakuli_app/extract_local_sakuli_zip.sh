#!/bin/bash

set -x

SAKULI_PROJECT_HOME=~/git-files/sakuli
SAKULI_INST_FOLDER=$SAKULI_PROJECT_HOME/sakuli_app
SAKULI_TARGET_DIR=$SAKULI_PROJECT_HOME/src/core/target/install
SAKULI_VERSION=0.9.2-SNAPSHOT

rm -rf $SAKULI_INST_FOLDER

echo "copy SAKULI installation to: $SAKULI_INST_FOLDER"
unzip $SAKULI_TARGET_DIR/sakuli-v$SAKULI_VERSION.zip -d $SAKULI_INST_FOLDER

echo "current dir $(dirname $0)"
cp $(dirname $0)/start_*.sh $SAKULI_INST_FOLDER
cp $(dirname $0)/run_*.sh $SAKULI_INST_FOLDER
chmod -v +x $(find  /home/tschneck/git-files/sakuli/sakuli_app/ -name "*.sh")