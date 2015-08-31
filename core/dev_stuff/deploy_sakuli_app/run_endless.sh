#!/bin/bash

i="0"
MAX=999
SLEEP_SEC=300

while [ $i -lt $MAX ]
do
    echo ">>>>>>>>>>>>>> RUN SAKULI test $i of $MAX <<<<<<<<<<<<<<<<"
    $(dirname $0)/start_*.sh
    echo "sleep $SLEEP_SEC sec.!"
    sleep $SLEEP_SEC
    i=$[$i+1]
done
