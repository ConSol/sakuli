#!/usr/bin/env bash

function fixPermission () {
    echo "fix all permissions issues (mount things to /opt/maven)"
    ls -la /opt/maven
    chmod -R a+rw /opt/maven
}

## start vnc display also

/root/scripts/vnc_startup.sh "$@"
my_exit_code=$?
fixPermission

exit $my_exit_code


