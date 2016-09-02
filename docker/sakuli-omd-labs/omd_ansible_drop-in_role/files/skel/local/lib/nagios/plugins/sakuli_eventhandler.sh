#!/bin/bash

LOG=${OMD_ROOT}/var/log/sakuli_screenshots.log
IMG_ROOT=${OMD_ROOT}/var/sakuli/screenshots
TMP=${OMD_ROOT}/tmp/sakuli
NOW=$(date)

STATE=$1
HOST=$2
SERVICE=$3
LASTSERVICECHECK=$4

mkdir -p $IMG_ROOT
mkdir -p $TMP

for d in $(find $IMG_ROOT -mindepth 3 -maxdepth 3 -mtime +30 -type d); do rm -rf $d; done

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
    [[ $OUT =~ "EXCEPTION" ]] || exit 0
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
    IMG_DIR="$IMG_ROOT/$HOST/$SERVICE/$LASTSERVICECHECK"
    echo "IMG_DIR: $IMG_DIR" >> $LOG
    mkdir -p "$IMG_DIR"
    mv $TMP/${TMPNAME} "$IMG_DIR/screenshot.${FORMAT}"
    echo "$OUT" > "$IMG_DIR/output.txt"
    ls -la "$IMG_DIR" >> $LOG
    ;;
esac
exit 0
