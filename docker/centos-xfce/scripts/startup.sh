#!/bin/bash

#resolve_vnc_connection
VNC_IP=$(ip addr show eth0 | grep -Po 'inet \K[\d.]+')
VNC_PORT="590"${DISPLAY:1}

##change vnc password
echo "change vnc password!"
(echo $VNC_PW && echo $VNC_PW) | vncpasswd


##start vncserver and show logfile
echo -e "\nStart VNCSERVER on DISPLAY= $DISPLAY \n\t=> connect via VNC viewer wiht $VNC_IP:$VNC_PORT"
vncserver $DISPLAY -depth $VNC_COL_DEPTH -geometry $VNC_RESOLUTION
/etc/xdg/xfce4/xinitrc &
sleep 2
$SAKULI_HOME/bin/sakuli.sh --run $SAKULI_TEST_SUITE
tail -f $SAKULI_TEST_SUITE/_logs/_sakuli.log