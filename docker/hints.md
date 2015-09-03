### update permissions
sudo chmod 755 /etc/X11/xinit/xinitrc

sudo chmod -R 755 /usr/share/fonts
[linux forum](http://www.linuxforums.org/forum/ubuntu-linux/93755-stating-xfce-vnc-scession.html)

### start vncserver
`vncserver -kill :1;  vncserver -depth 24 -geometry 1280x1024; sleep 5; cat ~/.vnc/*\:1.log`


### Info
* vnc seesions will use local settings, so it's not possible tu run xfce and gnome based session in parallel

### Port-Range
10.000 - 10.1000 für OpenStack freigegeben.


### CentOs
* Centos6: dbus id `/bin/dbus-uuidgen > /var/lib/dbus/machine-id`
* Centos7: dbus id `/bin/dbus-uuidgen > /etc/machine-id`