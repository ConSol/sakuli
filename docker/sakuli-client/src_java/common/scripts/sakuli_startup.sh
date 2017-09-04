#!/bin/bash
###
# Startup Script for the Sakuli headless testing container
###

# have to be added to hold all env vars correctly
# also will source $STARTUPDIR/generate_container_user
source $HOME/.bashrc

echo -e "\n\n------------------ START SAKULI CONTAINER ---------------------------"

## start UI and VNC components
$STARTUPDIR/vnc_startup.sh echo "VNC ready!"
echo -e "\n\n------------------ VNC STARTUP finished -----------------------------"
#env
echo -e "\n\n------------------ SAKULI TEST EXECUTION start ----------------------"
echo "Executing: '$@'"
echo MAVEN_OPTS=$MAVEN_OPTS
# have to be added to hold all env vars correctly

exec "$@"