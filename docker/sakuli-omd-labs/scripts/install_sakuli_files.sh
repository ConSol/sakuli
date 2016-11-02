#!/bin/bash
SAKULI_SRC=/tmp/sakuli/src/common/src/main/resources/org/sakuli/common/setup/nagios
TMPDIR=/tmp/omdfiles
OMDDEMO=/omd/sites/demo

function main() {
        # Update all files for gearman-proxy
        mkdir_cp $SAKULI_SRC/gearman_proxy/sakuli_gearman_proxy.cron $TMPDIR/etc/cron.d/
        mkdir_cp $SAKULI_SRC/gearman_proxy/sakuli_gearman_proxy.cfg $TMPDIR/etc/mod-gearman/
        mkdir_cp $SAKULI_SRC/gearman_proxy/sakuli_gearman_proxy.pl $TMPDIR/local/bin/

        # Update all files for screenshot history
        mkdir_cp $SAKULI_SRC/screenshot_history/sakuli_screenshots.conf $TMPDIR/etc/apache/conf.d/
        mkdir_cp $SAKULI_SRC/screenshot_history/sakuli_screenshot_eventhandler.cfg $TMPDIR/etc/nagios/conf.d/
        mkdir_cp $SAKULI_SRC/screenshot_history/sakuli_screenshot_eventhandler.sh $TMPDIR/local/lib/nagios/plugins/
        mkdir_cp $SAKULI_SRC/screenshot_history/*.ssi $TMPDIR/etc/thruk/ssi/
        mkdir_cp $SAKULI_SRC/screenshot_history/sakuli_action_menu.conf $TMPDIR/etc/thruk/thruk_local.d/

        # Update Sakuli DB check
        mkdir_cp $SAKULI_SRC/CheckMySQLHealthSakuli.pm $TMPDIR/etc/check_mysql_health/

        # Update PNP4Nagios Sakuli template
        mkdir_cp $SAKULI_SRC/check_sakuli.php $TMPDIR/etc/pnp4nagios/templates/

        # Nagios generic objects
        mkdir_cp $SAKULI_SRC/sakuli_nagios_objects.cfg $TMPDIR/etc/nagios/conf.d/
}

function mkdir_cp() {
        FILE=$1
        DIR=$2
        [ ! -d $DIR ] && mkdir -p $DIR
        cp $FILE $DIR
}

main
