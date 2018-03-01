#!/bin/bash
###
# Startup Script for the Sakuli headless testing container
###

# have to be added to hold all env vars correctly
# also will source $STARTUPDIR/generate_container_user
source $HOME/.bashrc
set -e
set -u

echo -e "\n\n------------------ START SAKULI UI CONTAINER ---------------------------"

args="$@"
if [[ "$1" == -* ]]; then
    args="${SAKULI_ROOT_DIR} $@"
fi

javaARG="-Djava.security.egd=file:/dev/./urandom -Dsakuli.ui.root.directory=$args -jar ${SAKULI_UI_INST_DIR-/app}/sakuli-ui-web.jar"

echo "Executing: 'java $javaARG'"
java -Dsecurity.default-username=${SAKULI_UI_USER} -Dsecurity.default-password=${SAKULI_UI_PW} $javaARG

echo -e "\n\n------------------ FINISHED SAKULI UI CONTAINER ------------------------"