### RDP peculiarities
#### things to know

There are four ways to connect to and work on a Sakuli client machine:

1. **VNC**
2. **Console** of a virtualization platform (ESX, Virtualbox, etc.)
3. **Remote Desktop** (Windows)
4. **local screen** 

For case 1. and 2. there is nothing special to watch out for, except that the screen must not be locked (otherwise Sikuli will also see a locked screen). The screen content will be the same as displays on a local screen (4.). 

For RDP on Windows there are some special things to know. Connecting to the Sakuli test client via RDP **locks any existing local console session of that user** and **attaches (="moves") it to a RDP session**.

Sakuli will just as well run within that RDP session. But closing/disconnecting/logging of that RDP session will not unlock the local console session again. Sakuli will see the same as a regular user: nothing but a locked screen. Read the next paragraph to learn how to avoid this. 

#### LOGOFF.bat
To log off a RDP session, right-click `%SAKULI_HOME%\bin\helper\LOGOFF.bat` and execute the script with administrator privileges. The script then

* determines the current RDP session ID
* redirects this session back to the local console
* terminates the RDP session.

#### check_logon_session.ps1

In `%SAKULI_HOME%\setup\nagios` you can find **check_logon_session.ps1** which can be used as a client-side Nagios check to ensure that the Sakuli user is always logged on, either via RDP or on the local console. Instructions for the implementation of this check can be found in the script header.

Define a service dependency of all Sakuli checks to this logon check; this will ensure that a locked session will not raise false alarms.

![](images/userloggedin.jpg)
