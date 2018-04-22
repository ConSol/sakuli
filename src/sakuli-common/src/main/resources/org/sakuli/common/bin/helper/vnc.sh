#!/bin/bash
CUR_YEAR=`date +'%Y'`
ROBOTIC_ICON=$SAKULI_HOME/bin/resources/robotic1.png
VNC_RESOLUTION="1024x768"
VNC_COL_DEPTH=24
VNC_DISPLAY=1

options=$@
arguments=($options)
index=0

DO_VNC_KILL=false

print_help() {
        cat <<USAGE
Sakuli VNC startup script
$CUR_YEAR - The Sakuli team.
http://www.sakuli.org
https://github.com/ConSol/sakuli

Opens a x-server VNC session under the defined display.

        vnc.sh [OPTIONS]
 -display           display number in headless mode (default: 1)
 -h,--help          show this help
 -kill              kills the vnc session of the defined display
USAGE
        exit 1
}

vnc_kill() {
        echo "kill vnc server on DISPLAY :$VNC_DISPLAY"
        vncserver -kill :$VNC_DISPLAY
}

notify() {
        if [[ -x $(which notify-send) ]]; then
                notify-send -t 10 -u low -i "$ROBOTIC_ICON" "$1"
        fi
        echo "$1"
}

init_vnc() {
        notify "init"
        if [[ ! -x $(which vncserver) ]]; then
                notify "Cannot start in headless mode: can't locate vncserver."
                exit 1
        fi
        PORT="590"$VNC_DISPLAY
        notify "vnc session starting now on display $VNC_DISPLAY (localhost:$PORT)."
        vncserver :$VNC_DISPLAY -depth $VNC_COL_DEPTH -geometry $VNC_RESOLUTION
        exit $?
}

let lastindex=${#arguments[@]}
i=0
while [ $i -le $lastindex ]; do
        case ${arguments[$i]} in
                "-h" | "-?" | "--help")
                    print_help
                    exit 0
                    ;;
                "-kill")
                    DO_VNC_KILL=true
                    let "i=$i+1"
                    ;;
                "-display" )
                    let "j=$i+1"
                    VNC_DISPLAY=${arguments[$j]}
                    let "i=$i+2"
                    ;;
                "")
                    let "i=$i+1"
                    ;;
                *)
                    notify "could not parse argument '${arguments[$i]}' see help 'vnc.sh --help'"
                    echo "-----------------------------"
                    print_help
                    exit -1
                    ;;
        esac
done

echo "kill $DO_VNC_KILL"
if $DO_VNC_KILL ; then
    vnc_kill
    exit 0
fi
init_vnc
