#!/bin/sh

#resolve_vnc_connection
VNC_IP=$(ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}')
VNC_PORT="590"$DISPLAY

##change vnc password
echo "change vnc password!"
/root/scripts/chvncpasswd.sh

##start vncserver and show logfile
echo "Start VNCSERVER on DISPLAY= $DISPLAY => connect via VNC viewer wiht $VNC_IP:$VNC_PORT"
vncserver $DISPLAY -depth $VNC_COL_DEPTH -geometry $VNC_RESOLUTION
/etc/xdg/xfce4/xinitrc &
sleep 5
tail -f /root/.vnc/*$DISPLAY.log