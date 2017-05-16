# Additional settings

This page contains different topics regarding the configuration of both **Sakuli** and its components, **Sahi** and **Sikuli**.

## Sakuli settings

### General note on property settings

**Sakuli properties** are predefined in `__SAKULI_HOME__/config/sakuli-default.properties`; these values should/can be **overridden** in the following order (last match wins): 

1. as a **global testsuite property** in `test-suites-folder/sakuli.properties`
   -> valid for **all test suites** within this folder
2. as a **testsuite property** in `test-suites-folder/test-suite/testsuite.properties`
   -> valid for **all test cases** within a test suite 
3. as a **Java VM option** like  `-D log.level.sakuli=DEBUG`, as option of the Sakuli starter
   -> valid for only one run

We do not recommend to change any values in `__SAKULI_HOME__/config/sakuli-default.properties` as a new version of Sakuli will have its own default property file; your changes will not be preserved. 

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
    

## Sikuli settings
### Highlighting

* **`sakuli.highlight.seconds`**=`1.1f` - duration for auto-highlighting and `highlight()` method 
* **`sakuli.autoHighlight.enabled`**=`false` - If true, every region gets highlighted automatically for `sakuli.highlight.seconds`


## Sahi settings
### Browser configuration
If the Sahi dashboard does not show any browser or if you want to add another browser to the dashboard…

![nobrowser](../docs/images/w_sahi_no_browser.jpg) 

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

