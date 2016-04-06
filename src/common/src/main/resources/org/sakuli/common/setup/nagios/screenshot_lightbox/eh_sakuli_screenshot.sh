#!/bin/bash
#

IMG_ROOT=${OMD_ROOT}/var/sakuli/screenshots

STATE=$1
HOST=$2
SERVICE=$3
LASTSERVICECHECK=$4
DAYS=${5:-30}

case $STATE in
"OK")
        ;;
"WARNING")
        ;;
"UNKNOWN")
        ;;
"CRITICAL")
        OUT=$(echo -e "GET services\nColumns: plugin_output\nFilter: host_name = $HOST\nFilter: description = $SERVICE" | unixcat ~/tmp/run/live)
        [[ $OUT =~ "EXCEPTION" ]] || exit 0
        OUTLONG=$(echo -e "GET services\nColumns: long_plugin_output\nFilter: host_name = $HOST\nFilter: description = $SERVICE" | unixcat ~/tmp/run/live)
        IMG=$(echo $OUTLONG | sed 's/^.*base64,\(.*\)".*$/\1/')
        #src="data:image/jpg
        FORMAT=$(echo $OUTLONG | sed 's/^.*data:image\/\(.*\);.*$/\1/' )

        #echo "$IMG" | base64 -d > $IMG_DIR/screenshot.${FORMAT}
        TMPNAME=screenshot_${LASTSERVICECHECK}.${FORMAT}
        echo "$IMG" | base64 -d > /tmp/${TMPNAME}
        # exit if no image data detected
        file /tmp/${TMPNAME} | grep -q 'image data' || exit 0
        IMG_DIR="$IMG_ROOT/$HOST/$SERVICE/$LASTSERVICECHECK"
        mkdir -p $IMG_DIR
        mv /tmp/${TMPNAME} $IMG_DIR/screenshot.${FORMAT}
        echo "$OUT" > $IMG_DIR/output.txt

        ;;
esac

# Clean up all screenshot directories older than X days
for d in $(find $IMG_ROOT -mindepth 3 -maxdepth 3 -mtime +$DAYS -type d); do rm -rf $d; done

exit 0
