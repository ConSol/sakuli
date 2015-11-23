#!/bin/bash

/root/scripts/vnc_startup.sh

# modify $SAKULI_TEST_SUITE permissions to ensure, that volume-mounted log files can be deleted afterwards
chmod -R a+rw $SAKULI_TEST_SUITE

# argument of sakuli.sh start with a dash.
# If not, assume that CMD was not meant as an argument
# for sakuli.sh (=ENTRYPOINT). Hence, try to execute CMD standalone.
if [ "${1:0:1}" == "-" ]; then
        $SAKULI_HOME/bin/sakuli.sh $*
else
        exec $1
fi

res=$?
echo "SAKULI_RETURN_VAL: $res"
exit $res
