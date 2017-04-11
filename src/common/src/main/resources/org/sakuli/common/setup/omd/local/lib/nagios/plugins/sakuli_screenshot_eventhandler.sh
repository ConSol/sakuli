#!/bin/bash


STATE=$1
HOST=$2
SERVICE=$3
LASTSERVICECHECK=$4

LOG=${OMD_ROOT}/var/log/sakuli_screenshot_eventhandler.log
IMG_ROOT=${OMD_ROOT}/var/sakuli/screenshots/$HOST/$SERVICE
TMP=${OMD_ROOT}/tmp/sakuli
NOW=$(date)

mkdir -p $TMP

case $STATE in
"OK")
    ;;
"WARNING")
    ;;
"UNKNOWN")
    ;;
"CRITICAL")
    echo "$NOW ------------------" >> $LOG
    echo "Host/Service: $HOST / $SERVICE"  >> $LOG
    echo "State: $STATE" >> $LOG
    echo "LASTSERVICECHECK: $LASTSERVICECHECK" >> $LOG

    OUT=$(echo -e "GET services\nColumns: plugin_output\nFilter: host_name = $HOST\nFilter: description = $SERVICE" | unixcat ~/tmp/run/live)
    OUTLONG=$(echo -e "GET services\nColumns: long_plugin_output\nFilter: host_name = $HOST\nFilter: description = $SERVICE" | unixcat ~/tmp/run/live)

    echo "OUT: $OUT" >> $LOG

    IMG=$(echo $OUTLONG | sed 's/^.*base64,\(.*\)".*$/\1/')
    #src="data:image/jpg
    FORMAT=$(echo $OUTLONG | sed 's/^.*data:image\/\(.*\);.*$/\1/' )

    echo "Format: $FORMAT" >> $LOG
    TMPNAME=screenshot_${LASTSERVICECHECK}.${FORMAT}
    echo "TMPNAME: $TMPNAME" >> $LOG
    echo "$IMG" | base64 -d > $TMP/${TMPNAME}
    # exit if no image data detected
    file $TMP/${TMPNAME} | grep -q 'image data' || exit 0
    IMG_DIR="$IMG_ROOT/$LASTSERVICECHECK"
    echo "IMG_DIR: $IMG_DIR" >> $LOG
    mkdir -p "$IMG_DIR"
    mv $TMP/${TMPNAME} "$IMG_DIR/screenshot.${FORMAT}"
    echo "$OUT" > "$IMG_DIR/output.txt"
    ls -la "$IMG_DIR" >> $LOG
    ;;
esac
exit 0
