# Additional settings

This page contains different topics regarding the configuration of both **Sakuli** and its components, **Sahi** and **Sikuli**.

## Sakuli settings

### General note on property settings

**Sakuli properties** are predefined in `__SAKULI_HOME__/config/sakuli-default.properties`; these values should/can be **overridden** in the following order (last match wins): 

1. as a **global testsuite property** in `test-suites-folder/sakuli.properties`
   -> valid for **all test suites** within this folder
2. as a **testsuite property** in `test-suites-folder/test-suite/testsuite.properties`
   -> valid for the **test suite** itself and **all test cases** within it 
3. as a **Java VM option** like  `-D log.level.sakuli=DEBUG`, as option of the Sakuli starter
   -> valid for only one test run

We do not recommend to change any values in `__SAKULI_HOME__/config/sakuli-default.properties` as a new version of Sakuli will have its own default property file and would overwrite the existing one; your changes would not be preserved. 

### Forwarder

Sakuli can hand over test result to "**Forwarder**", which can be currently **GearmanD** servers (such as Nagios monitoring systems with mod-gearman) and **JDBC databases**. If no forwarder is defined, a result summary is printed out in the end of a suite. 

![sakuli_forwarders](./pics/sakuli-forwarder.png)

For the configuration of forwarders on the OMD server side, see [Forwarder in OMD](installation-omd.md#forwarders)

  * [Setting up Sakuli client to send results to the **Database**](forwarder-database.md#sakuli-configuration)
  * [Setting up Sakuli client to submit results to the **Gearman Forwarder**](forwarder-gearman.md#sakuli-configuration)

### Exception handling

Some objects (*Region, Application, Environment*) allow on their creation to specify the optional boolean argument `resumeOnException`, which controls whether the script should resume on an exception which is related to the object or one of its method (default: false).

Setting this to *true* can be useful if you want to raise a custom exception or no exception at all. 

Set property **`sakuli.exception.suppressResumedExceptions`** to

   * `true`  = the exception will be logged and appear in the test result
   * `false` = the exception will NEITHER be logged NOR appear in the test result.

Example:
 
    // create region "foo"
    var foo = new Region("bar.png",true);
    // if "image" is not found, the script will resume
    var baz = foo.find("image");
    // throw your "own" exception.
    // If you do not, and suppressResumedExceptions=true, the exception will be suppressed.
    if (baz == null){
        throw "Sorry, I could not find image 'image'.";
    }

### Logging 

#### Verbosity

The logging verbosity of all compoments *Sakuli, Sahi, Sikuli, Spring* - and *Java* in general can be changed individually. 

("Verbosity" means one of the levels `DEBUG - INFO - WARN - ERROR` )

* **`log.level.sakuli`**`=__LEVEL__`  -  logging level for **Sakuli** output
* **`log.level.sikuli`**`=__LEVEL__`  -  logging level for **Sikuli** output
* **`log.level.sahi`**`=__LEVEL__`  -  logging level for **Sahi** output
* **`log.level.spring`**`=__LEVEL__`  -  logging level for the **Spring** framework (only used internally)
* **`log.level.root`**`=__LEVEL__`  -  logging level for all other **Java classes and libraries**

#### Log file folder / pattern

* **`sakuli.log.pattern`**`=%-5level [%d{YYYY-MM-dd HH:mm:ss.SSS}] - %msg%n`  -  Format string 
* **`sakuli.log.folder`**`=${sakuli.testsuite.folder}/_logs`  -  Log folder

In general it is also possible to add your own Logback configuration under `__SAKULI_HOME__/config/sakuli-log-config.xml`. For more information about the Logback syntax please refer to the [Logback manual](http://logback.qos.ch/manual/configuration.html).

#### Log exception format

* **`sakuli.log.exception.format`**`=TypeError (.*)`  -  Format regular expression 

Optional custom exception message logging format expression. This is a regular expression that formats all exception messages before they get printed to results or forwarded to external systems like OMD Gearman. The regular expression works on groups that identify
the part of the message that should be included. You can use multiple groups in order to extract multiple message snippets to the final result

For example if we have following error message given:

    Something went wrong!
    Now some important information:
    TypeError el is undefined AccessError el is not accessible
    Some more details follow now ...
    
We can extract the important information with this regular expression: **TypeError(.*).*AccessError\s(.*)**

Now we get the final error message: **el is undefined el is not accessible**

The log exception format property supports multiple expression values. You can use a JSON array syntax when having more than one regular expression that should be evaluated.

* **`sakuli.log.exception.format`**`=["TypeError(.*)", "AccessError(.*)", "OtherError(.*)"]`  -  Multiple format regular expressions 

All regular expressions in the list are evaluated in sequence and the results get combined to a single formatted exception message.

We can also use custom error messages in order to customize the resulting exception log message. You can map the exception log format expressions to a custom text like this:
 
* **`sakuli.log.exception.format`**`=["TypeError(.*) => Type error: %s", "AccessError(.*) => Access error: %s", "OtherError.* => Some other error occurred"]`  -  Multiple format regular expressions with custom text mappings
 
The exception log expression is mapped to a custom text using the "=>" operator. So the regular expression 'TypeError(.*)' is mapped to the custom message 'Type error: %s'. The custom text is able to use regular expression back 
references. Just use simple Java message format parameters (e.g. '%s') in your custom error message in order to reference regular expression groups as back reference.
 
Multiple back references are supported, too. Respectively use indices when using the Java String format parameters in your custom error message ('%1s', '%2s' and so on). When no regular expression group is used you can simple map 
the regular expression match to a custom message as shown in the expression: 'OtherError.* => Some other error occurred'.

#### Log file rotation 

* **`sakuli.log.maxAge`**`14` - Deletes all files that are older than (default) 14 days in the defined `sakuli.log.folder`.

On Linux you can additional configure **logrotate** to tidy up old log files: 

    vim /etc/logrotate.d/sakuli
    
    __SUITE_FOLDER__/*/_logs/* __SUITE_FOLDER__/*/_logs/_screenshots/* {
        size 1k
        missingok
        maxage 2
    }



### Secret De-/Encryption
#### Interface selection

Neither Sahi nor Sikuli have a way to prevent **sensible data** (passwords, PINs, etc.) from being logged and stored in the script in **clear text**. 

That's the reason why Sakuli is able to **encrypt** them on the command line, and to **decrypt** them again on runtime just for the moment when they are needed. There is no (quick) way to decrypt those secrets again on the command line, so this is rather a way to obscure things not everybody should see than high-secure a encryption mechanism. 

Among other parameters, Sakuli uses the MAC address of a local network interface card as a encryption salt. Hence no virtual adapters can be choosen. 

You can decide whether Sakuli should automatically select an adapter...

* **`sakuli.encryption.interface.autodetect`**`=true`

..or a specific one should be used: 

* **`sakuli.encryption.interface.autodetect`**`=false`
* **`sakuli.encryption.interface`**`=eth0`

#### Encrypt a secret

To encrypt secrets on the command line, Sakuli uses the MAC address of a NIC on the local machine (Windows/Linux). The following command lets Sakuli decide which NIC will be used: 

    sakuli encrypt foo
    =========== Calling Sakuli JAR: java -classpath C:\Program Files (x86)\sakuli\sakuli-v0.9.3-SNAPSHOT\libs\jav\sakuli.jar;C:\Program Files (x86)\sakuli\sakuli-v0.9.3-SNAPSHOT\libs\java\* org.sakuli.starter.SakuliStarter --sakuli_home C:\Program Files (x86)\sakuli\sakuli-v0.9.3-SNAPSHOT --encrypt foo ===========

    String to Encrypt: foo
    ...
    Encrypted secret with interface 'eth3': CKXIAZmO7rSoBVMGgJZPDQ==
   
    ... now copy the secret to your testcase!
    
Add `-interface eth0` to select eth0 as salt interface.  Add `-interface list` to get a list of all available adapters. 

#### Decrypt a secret

To decrypt and use a secret in Sakuli test cases, use one of the following methods:

* [pasteAndDecrypt](./api/sakuli_Environment.md#pasteanddecrypttext)
* [typeAndDecrypt](./api/sakuli_Environment.md#typeanddecrypttext-optmodifiers)
* [decryptSecret](./api/sakuli_Environment.md#decryptsecretsecret)


### Screenshot settings

To set the format and destination folder for screenshots taken by Sakuli:

* **`sakuli.screenshot.onError`**=`true` - take a screenshot in case of an exception 
* **`sakuli.screenshot.dir`**`=${sakuli.log.folder}/_screenshots`  -  folder for screenshot files (if activated)
* **`sakuli.screenshot.format`**`=jpg`  -  screenshot file format (Possible values: jpg, png)
* **`sakuli.forwarder.gearman.nagios.template.screenshotDivWidth`**=`640px` - Screenshot dimensions for results sent to Gearmand
    
### RDP pecularities
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

![](pics/userloggedin.jpg)

## Sikuli settings
### Highlighting

* **`sakuli.highlight.seconds`**=`1.1f` - duration for auto-highlighting and `highlight()` method 
* **`sakuli.autoHighlight.enabled`**=`false` - If true, every region gets highlighted automatically for `sakuli.highlight.seconds`


## Sahi settings
### Browser configuration
If the Sahi dashboard does not show any browser or if you want to add another browser to the dashboard…

![nobrowser](../docs/pics/w_sahi_no_browser.jpg) 

…you have to edit `__SAHI_DIR__/userdata/config/browser_types.xml`. Each browser is defined within a **browserType** block. Please refer to the [Sahi Documentation, "Configure Browsers"](https://sahipro.com/docs/using-sahi/sahi-configuration-basic.html) to see the *browserType* Nodes for popular browsers. 

For **PhantomJS** please save [sahi.js](http://labs.consol.de/sakuli/install/3rd-party/phantom/sahi.js) into the folder `__SAHI_DIR__\phantomjs\` and use this option line: 

        	<options>--proxy=localhost:9999 __SAHI_DIR__\phantomjs\sahi.js</options> 

Attention: PhantomJS 2 is currently unsupported. Use version 1.9.x
	
### Browser selection 

You may want to change the browser due to the following reasons: 

* to check if a web test (made with Sahi methods) for browser A is also running properly on browser B
* to run a headless browser
    * just for curiosity :-)
    * to keep the browser in background while Sakuli tests a non-web application (e.g. fat client)  

In addition to the possibilities described in [General note on property settings](./additional-settings.md#general-note-on-property-settings), the generic Sakuli starter `sakuli/sakuli.exe` can also be given the parameter `-browser`: 

    sakuli run /path/to/suite -browser chrome 

### Sahi behind a proxy

Set the following properties (as global in the sakuli.properties) to define a proxy Sahi should connect to.  

    ```
	### HTTP/HTTPS proxy Settings
	### Set these properties, to enable the test execution behind company proxies
	# Use external proxy server for HTTP* 
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

