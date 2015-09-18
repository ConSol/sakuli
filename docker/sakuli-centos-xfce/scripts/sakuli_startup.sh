#!/bin/bash

# call `vnc_startup.sh` from the root container
/root/scripts/vnc_startup.sh

# modify $SAKULI_TEST_SUITE permissions to ensure, that volume-mounted log files can be deleted afterwards
find $SAKULI_TEST_SUITE -type d | while read dir ; do chmod 777 "$dir" ; done
find $SAKULI_TEST_SUITE -type f | while read file ; do chmod 666 "$file" ; done

$SAKULI_HOME/bin/sakuli.sh --run $SAKULI_TEST_SUITE