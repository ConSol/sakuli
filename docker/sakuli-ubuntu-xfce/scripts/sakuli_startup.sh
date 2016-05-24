#!/bin/bash

echo "$@"

# source common functions
. ~/scripts/.sakuli_functions

# start VNC server
vnc_init() {
        /root/scripts/vnc_startup.sh
}

if [ ! $# -gt 0 ]; then
        echo "Wrong number of arguments!"
        sakuli -help
        exit 1
fi

# Sakuli starter COMMANDs start with `run` or `encrypt` and a option with a dash.
# If not, assume that CMD was not meant as an argument for sakuli (=ENTRYPOINT).
if [[ $1 =~ run|encrypt|help|version ]]; then
#        if [ "$1" == "run" ]; then
                pushd $2; sync_browser_profile; popd
                vnc_init
#       fi
        echo "Executing: 'sakuli $@'"
echo "$@"
        $SAKULI_HOME/bin/sakuli "$@"
        res=$?
        echo "SAKULI_RETURN_VAL: $res"
        # modify $SAKULI_TEST_SUITE permissions to ensure that volume-mounted log files can be deleted afterwards
        [ "$1" == "run" ] && chmod -R a+rw $2
        exit $res
else
        # Do not execute Sakuli but any other command
        vnc_init
        exec $1
fi
