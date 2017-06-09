#!/bin/bash

. $OMD_ROOT/etc/omd/site.conf

STATE=$1
HOST=$2
SERVICE=$3
LASTSERVICECHECK=$4

LOG=${OMD_ROOT}/var/log/sakuli_screenshot_eventhandler.log
IMG_ROOT=${OMD_ROOT}/var/sakuli/screenshots/$HOST/$SERVICE
TMP=${OMD_ROOT}/tmp/sakuli

hash file 2>/dev/null || { log "ERROR: The file command is not installed.  Aborting." ; exit 1; }

function log() {
        echo "[$$] `date +"%x %X"` $1" >> $LOG
}

mkdir -p $TMP

case $STATE in
"OK")
    ;;
"WARNING")
    ;;
"UNKNOWN")
    ;;
"CRITICAL")
    log "---------------------------------------"
    log "HOST/SERVICE: $HOST / $SERVICE"
    log "STATE: $STATE"
    log "LASTSERVICECHECK: $LASTSERVICECHECK"

    OUT=$(echo -e "GET services\nColumns: plugin_output\nFilter: host_name = $HOST\nFilter: description = $SERVICE" | unixcat ~/tmp/run/live)
    OUTLONG=$(echo -e "GET services\nColumns: long_plugin_output\nFilter: host_name = $HOST\nFilter: description = $SERVICE" | unixcat ~/tmp/run/live)

    log "PLUGIN_OUT: $OUT"

    IMG=$(echo $OUTLONG | sed 's/^.*base64,\(.*\)".*$/\1/')
    #src="data:image/jpg
    FORMAT=$(echo $OUTLONG | sed 's/^.*data:image\/\(.*\);.*$/\1/' )

    log "Found screenshot format: $FORMAT"
    TMPNAME=screenshot_${LASTSERVICECHECK}.${FORMAT}
    echo "$IMG" | base64 -d > $TMP/${TMPNAME}

    # exit if no image data detected
    file $TMP/${TMPNAME} | grep -q 'image data' || { log "$TMP/$TMPNAME is not a valid image file. Exiting."; exit 0; }
    IMG_DIR="$IMG_ROOT/$LASTSERVICECHECK"
    log "IMG_DIR: $IMG_DIR"
    mkdir -p "$IMG_DIR"
    log "Moving $TMP/${TMPNAME} to $IMG_DIR/screenshot.${FORMAT}"
    mv $TMP/${TMPNAME} "$IMG_DIR/screenshot.${FORMAT}"
    echo "$OUT" > "$IMG_DIR/output.txt"

    # write image path to InfluxDB
    if [ "$CONFIG_INFLUXDB"x == "onx" ]; then
        log "Writing image path to InfluxDB..."
        eval $(grep '^USER=' $OMD_ROOT/etc/init.d/influxdb)
        eval $(grep '^PASS=' $OMD_ROOT/etc/init.d/influxdb)
        OUT=$(curl -v -XPOST 'http://'$CONFIG_INFLUXDB_HTTP_TCP_PORT'/write?db=sakuli&u='$USER'&p='$PASS'&precision=s' --data-binary 'images,host='$HOST',service='$SERVICE' path="<div class=\"sakuli-popup\">/sakuli/'$HOST'/'$SERVICE'/'$LASTSERVICECHECK'/</div>" '$LASTSERVICECHECK  2>&1 | grep '< HTTP')
        log "InfluxDB responded: $OUT"
    fi

    ;;
esac
exit 0
