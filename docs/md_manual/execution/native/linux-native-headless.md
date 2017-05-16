# Headless Sakuli checks on Linux

Running Sakuli on the user desktop is nice, but has the drawback that the session gets highjacked on each Sakuli run. Moving the mouse e.g. can have negative effects on the test execution. 

For that reason we advise to run Linux based Sakuli checks in in one of the following modes: 

* in a virtual display (="headless"), which is straightforward in Linux (documented on this page)
* in a [Docker container](docker-images.md) (more scalable)

## Installing and configuring the VNC server

On **Ubuntu**, first **install** vnc4server: 

```bash
sudo apt-get install vnc4server
```
    
Start vncserver for the first time to create a **session password**: 
    
```bash
~$ vncserver

You will require a password to access your desktops.
Password:
Verify:

New 'sakulidemo:1 (sakuli)' desktop is sakulidemo:1

Creating default startup script __HOME__/.vnc/xstartup
Starting applications specified in __HOME__/.vnc/xstartup
Log file is __HOME__/.vnc/sakulidemo:1.log
```

`.vnc/xstartup` controls what to start within a xvnc session. Do not touch this file on OpenSUSE; on **Ubuntu** you have to replace its content with the following lines (because you are using  **gnome-session-fallback**, aren't you…?): 

```bash
~$ vim .vnc/xstartup  

#!/bin/sh
export XKL_XMODMAP_DISABLE=1
unset SESSION_MANAGER
unset DBUS_SESSION_BUS_ADDRESS

gnome-panel &
gnome-settings-daemon &
metacity &
```

Restart the current vnc sesssion:

```bash
~$ vncserver -kill :1 && vncserver
```
    
Now open a RDP client (on Ubuntu: *Applications - Internet - Remmina Remote Desktop Client*) and enter the connection data: 

* Protocol: VNC
* Server: localhost:5901
* Password: `__VNC_SESSION_PASSWORD__`

You should see now an empty GNOME/KDE desktop - started headless!


## Test

You are now ready to run the **minimal Sakuli check** in **headless (=VNC)** mode. Sakuli provides for this task a pre- and postHook script, which can be used like follow: 

On the **Ubuntu** desktop, open a terminal window and execute 

* on **Ubuntu**: `sakuli run __INST_DIR__/example_test_suites/example_ubuntu/ --vnc` 
* on **openSUSE**: `__SAKULI_HOME__/bin/sakuli.sh --run __INST_DIR__/example_test_suites/example_opensuse/ --vnc` 
 
You should see that Sakuli

1.  opens **Firefox**
2.  opens the **calculator** and calculates *525+100=625* 
3.  opens an **editor** and writes a **status message**

![](images/u_vnc_test.png)

## Scheduling by cron 

Add the following line to Sakuli's crontab: 

```bash
SAKULI_HOME=__SAKULI_HOME__
DISPLAY=:1

*/2 * * * * $SAKULI_HOME/bin/sakuli run $SAKULI_HOME/../example_test_suites/example_ubuntu -preHook $SAKULI_HOME/bin/helper/vnc.sh -postHook '$SAKULI_HOME/bin/helper/vnc.sh -kill' 2>&1 > /dev/null
```
