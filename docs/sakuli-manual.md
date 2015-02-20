# Sakuli Settings

This page contains different topics regarding the configuration and architecture of Sakuli. Although some of them will affect the behaviour of one of the underlying tools "Sahi" and "Sikuli", they will be collected here.

## Sahi Proxy settings
If web tests with Sakuli should go through your company's proxy, edit the property file `%SAKULI_HOME%/_include/sakuli.properties`, section __SAHI-SCRIPT-RUNNER-PROPERTIES__ for both http and https. `auth.username` and `auth.password` are only used if `auth.enable` is set to `true`.
Use the bypass list to exclude certain URLs from being accessed through the proxy.

    ```
	### HTTP/HTTPS proxy Settings
	### Set these properties, to enable the test execution behind company proxies
	# Use external proxy server for HTTP
	ext.http.proxy.enable=true
	ext.http.proxy.host=proxy.yourcompany.com
	ext.http.proxy.port=8080
	ext.http.proxy.auth.enable=false
	ext.http.proxy.auth.name=user
	ext.http.proxy.auth.password=password

	# Use external proxy server for HTTPS
	ext.https.proxy.enable=true
	ext.https.proxy.host=proxy.server.com
	ext.https.proxy.port=8080
	ext.https.proxy.auth.enable=false
	ext.https.proxy.auth.name=user
	ext.https.proxy.auth.password=password

	# There is only one bypass list for both secure and insecure.
	ext.http.both.proxy.bypass_hosts=localhost|127.0.0.1|*.internaldomain.com|www.verisign.com

    ```


## Logging properties

###1. Change the logging Level
*  Set the logging levele over __system properties__

	  Add to your strater script e. g. `START_testsuite.sh` at the call
	  ```
	  java -classpath ... org.sakuli.starter.SakuliStarter -run "$SAKULI_HOME/sakuli_test_suites/$SUITE" "$INCLUDE_FOLDER"
	  ```
	  one of the following Parameter:
	  * `-Dlog-level-sakuli=DEBUG` 	- logging level for the common Sakuli output
      * `-Dlog-level-sikuli=INFO` 	- logging level for the underlying Sikuli output
      * `-Dlog-level-sahi=INFO`     - logging level for the underlying Sahi output
      * `-Dlog-level-spring=INFO`   - logging level for the internal used spring-framework
      * `-Dlog-level-root=DEBUG`    - logging level for all other Java classes and libraries

*  To change the logging level constantly for all test executions, modify the file `%SAKULI_HOME%/_include/sakuli-log-config.xml`:

	```
	    ...
		<!-- default level INFO; -->
	    <logger name="org.sakuli" level="${log-level-sakuli:-DEBUG}"/>
	    ...
    ```

###2. Change the logging configuration
There are two places to configure the logging format/verbosity of Sakuli:

* `%SAKULI_HOME%/_include/sakuli.properties` contains the common logging settings for sakuli:

		# Log pattern for the logging output.
		#
		# Log pattern for development with java classes:
		# sakuli.log.pattern=%-5level %d{YYYY-MM-dd HH:mm:ss.SSS} [%thread]  %logger{36} - %msg%n
		sakuli.log.pattern= %-5level [%d{YYYY-MM-dd HH:mm:ss.SSS}] - %msg%n

		# Sets the output folder for the log files
		sakuli.log.folder=${sakuli.testsuite.folder}/_logs


* `%SAKULI_HOME%/_include/sakuli-log-config.xml` allows more detailled configuration of the logging format in [Logback](http://logback.qos.ch/) syntax. For more detailed information, see
[Logback configuration](http://logback.qos.ch/manual/configuration.html).


## Screenshot settings

`%SAKULI_HOME%/_include/sakuli.properties` allows to set the format and destination folder for screenshots taken by Sakuli:

    # Sets the output folder for the error screenshots (if activated)
    sakuli.screenshot.dir=${sakuli.log.folder}/_screenshots
    # Output format for the take screenshots.
    # Possible values: jpg, png
    sakuli.screenshot.format=jpg
    #sakuli.screenshot.format=png


## Connecting to Sakuli clients

### Windows
#### connection types
There are a few way to connect to a Sakuli client machine:

1. VNC
2. Console of a virtualization platform (ESX, Virtualbox, etc.)
3. Remote Desktop
4. (perhaps more…)

Sakuli does not run (for the moment at least) headless; for that reason it is crucial that there is always a unlocked, "real" screen available.

For case 1. and 2. there is nothing special to watch out for, except that the screen must not be locked (see the [installation manual](../docs/installation-windows.md)). Sakuli is always running on the so-called *local console* (what the OS sends out on the graphics adapter).

#### RDP pecularities
For RDP, there are some special things to know. Connecting to the Sakuli test client via RDP **locks any existing local console session of that user** and **attaches (="moves") it to a RDP session**.

Sakuli will also run within that RDP session. But closing/disconnecting/logging of that RDP session will not unlock the local console session again. Sakuli will see the same as a regular user: the famous blue lock screen.

##### LOGOFF.bat
As described in the [Windows 7 installation manual](../docs/installation-windows.md), use instead **LOGOFF.bat** on the Desktop to disconnect the session (right-click and execute it with Administrator privileges!). This will

* first "move" the RDP session back to the local console
* an terminate the RDP session.

##### check_logon_session.ps1
In *sakuli\setup\nagios* you can find **check_logon_session.ps1** which can be used as a client-side check to ensure that the Sakuli user is always logged on, either via RDP or on the local console. Instructions for the imeplentation of this check can be found in the script header.

Define a service dependency of all Sakuli checks to this logon check; this will ensure that a locked session will not raise false alarms.

- - -

## Ubuntu
FIXME

- - -

## Secret De-/Encryption
### Encryption

(You probably came from [Installation (Windows 7)](../docs/installation-windows.md) or [Installation (Ubuntu)](../docs/installation-ubuntu.md)- if so, do the following section and jump back to the link mentioned at the end.)

To ensure that secrets (passwords, PIN, etc) never get logged in plain text, they can be encrypted on the command line; Sakuli then decrypts them on runtime e.g. in the function `env.decryptSecret()`. There is no (quick) way to decrypt secrets again on the command line. This is of course no high-secure encryption mechanism but rather a way to obscure things not everybody should see.

For the encryption, Sakuli uses among other parameters the MAC address of a given network interface card as a encryption salt. When you set up a new Sakuli client, the interface name will be auto-detected as long as the property `sakuli.encryption.interface.autodetect=true`. If you wan't to use a specific network interface for the encryption set your `sakuli.properties` or `testsuite.properties` like follow:
```
sakuli.encryption.interface.autodetect=false
sakuli.encryption.interface=eth0
```

To __encrypt a secret__ do the following steps on Windows _cmd.exe_ or the Unix _Shell_:

* __Windows__:
    * Encrypt with __auto-detection__:

      `%SAKULI_HOME%\scripts\helper\encrypt_password.bat somesecret`

      your output will be something like:
      ```
          String to Encrypt: somesecret
          ...
          Encrypted secret with interface 'eth3': THwLJK7ObjLkmoViCHm7lA==

          ... now copy the secret to your testcase!
          (interface determined by auto-detection)
      ```

    * Encrypt with __sepcific interface__ (e.g. eth8):

      `%SAKULI_HOME%\scripts\helper\encrypt_password.bat somesecret eth8`

      your output will be something like:
      ```
          String to Encrypt: somesecret
          ...
          Encrypted secret with interface 'eth8': bVKIUWcgaPDjasFf2uI15Q==

          ... now copy the secret to your testcase!
      ```

    * If "eth8" points to an interface with no valid MAC address (e.g. Virtual adapters), you will get a long error message starting with "Cannot resolve mac address".
     ![enc_error](../docs/pics/w_enc_error.jpg)
    * Select an interface with a valid MAC (here: eth3) and start the script again. The output should be now something like
     ![encrypted](../docs/pics/w_encrypted.jpg)
    * Remember the Interface name.


* __Linux__:

  The output will be similar to above one in Windows.
    * Encrypt with __auto-detection__: `$SAKULI_HOME/scripts/helper/encrypt_password.sh somesecret`

    * Encrypt with __sepcific interface__ (e.g. eth8): `$SAKULI_HOME/scripts/helper/encrypt_password.sh somesecret eth8`

    * Sometimes it is necessary to mark the the script as executable before you can run it: `chmod +x $SAKULI_HOME/scripts/helper/encrypt_password.sh`


If you came here during the [Installation on Windows 7](../docs/installation-windows.md) or [on Ubuntu](../docs/installation-ubuntu.md), go back there now.

Otherwise: the red arrow shows the encrypted string, which you can copy into the clipboard.

### Decryption

To decrypt and use a secret, use one of the following methods:

* [pasteAndDecrypt](./api/sakuli_Environment.md#pasteanddecrypttext)
* [typeAndDecrypt](./api/sakuli_Environment.md#typeanddecrypttext-optmodifiers)
* [decryptSecret](./api/sakuli_Environment.md#decryptsecretsecret)

- - -

## Making tests more reliable
### Killing orphaned processes
FIXME killproc.vbs
