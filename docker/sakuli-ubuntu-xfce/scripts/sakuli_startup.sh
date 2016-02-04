#!/bin/bash

/root/scripts/vnc_startup.sh

# modify $SAKULI_TEST_SUITE permissions to ensure, that volume-mounted log files can be deleted afterwards
modify_testsuite_permissions(){
    while test $# -gt 0; do
        case "$1" in
            -r|--run)
                shift #shift to suite parameter value
                chmod -R a+rw $1
                shift
                ;;
            *)
                shift
                continue
                ;;
        esac
    done
}

# argument of sakuli.sh start with a dash.
# If not, assume that CMD was not meant as an argument
# for sakuli.sh (=ENTRYPOINT). Hence, try to execute CMD standalone.
if [ "${1:0:1}" == "-" ]; then
        i=$(eval echo $*)
        echo "call 'sakuli.sh $i'"
        $SAKULI_HOME/bin/sakuli.sh $i

        res=$?
        echo "SAKULI_RETURN_VAL: $res"
        modify_testsuite_permissions $i
        exit $res
else
        exec $1
fi