#!/bin/bash
###
# Startup Script for the Sakuli headless testing container
###

# have to be added to hold all env vars correctly
source $STARTUPDIR/generate_container_user
if [ -f $HOME/.bashrc ] ; then
    source $HOME/.bashrc
fi

echo -e "\n\n------------------ START SAKULI CONTAINER ---------------------------"

## start UI and VNC components
$STARTUPDIR/vnc_startup.sh
echo -e "\n\n------------------ VNC STARTUP finished -----------------------------"
#env
echo -e "\n\n------------------ SAKULI TEST EXECUTION start ----------------------"
echo "Executing: 'sakuli $@'"
$SAKULI_HOME/bin/sakuli "$@"
res=$?
echo "SAKULI_RETURN_VAL: $res"
exit $res