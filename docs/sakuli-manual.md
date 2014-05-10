# Sakuli Manual

## Connecting to Sakuli clients

### Windows 
#### connection types
There are a few way to connect to a Sakuli client machine: 

1. VNC
2. Console of a virtualization platform (ESX, Virtualbox, etc.)
3. Remote Desktop

Sakuli does not run (for the moment at least) headless; for that reason it is crucial that there is always a unlocked, "real" screen available. 

For case 1. and 2. there is nothing special to watch out for, except that the screen must not be locked (see the [installation manual](../docs/installation-windows.md)). Sakuli is always running on the so-called *local console* (what the OS sends out on the graphics adapter).

#### RDP pecularities
For RDP, there are some special things to know. Connecting to the Sakuli test client via RDP **locks any existing local console session of that user** and **attaches (="moves") it to a RDP session**. 

Sakuli will also run within that RDP session. But closing/disconnecting/logging of that RDP session will not unlock the local console session again. Sakuli will see the same as a regular user: the famous blue lock screen. 

##### LOGOFF.bat
As described in the [Windows 7 installation manual](../docs/installation-windows.md)), use instead **LOGOFF.bat** on the Desktop to disconnect the session (right-click and execute it with Administrator privileges!). This will

* first "move" the RDP session back to the local console
* an terminate the RDP session.##### check_logon_session.ps1In *sakuli\setup\nagios* you can find **check_logon_session.ps1** which can be used as a client-side check to ensure that the Sakuli user is always logged on, either via RDP or on the local console. Instructions for the imeplentation of this check can be found in the script header. 
Define a service dependency of all Sakuli checks to this logon check; this will ensure that a locked session will not raise false alarms. 

### Ubuntu




## Encryption of a secret

If some test cases have the need to encrypt a secret, you have to do the following steps:

1. Encrypt your secret with the following command in the command line of your system:

    * unix

        ```
        java -classpath sakuli.jar:lib/* de.consol.sakuli.starter.SakuliStarter -encrypt yourSecrect -interface eth0
        ```

    * windows

         ```
         java -classpath sakuli.jar;lib\* de.consol.sakuli.starter.SakuliStarter -encrypt yourSecrect -interface eth0
         ```

2. Copy the encrypted secret in your clipoard.

3. Paste the encrypted secret in your test case into the function:

    ```
        sakuli.decryptSecret("yourEncryptedSecrect");
    ```