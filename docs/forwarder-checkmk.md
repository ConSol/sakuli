# Check_MK Forwarder

This page describes how to setup a Sakuli client to send results to a Check_MK monitoring system.

![sakuli-checkmk](pics/sakuli-checkmk.png)

## Sakuli client configuration

It is assumed that the client is already monitored by Check_MK and a agent is installed and running on it.

### Spool folder

Create a new folder ''spool'' in the installation path of the Check_MK agent. This is the folder where the results will be written to. The user which is used to run Sakuli checks must be given **write access** to this folder.

### Forwarder configuration

Now set the properties for the check_mk receiver. For this, edit `sakuli.properties` in the folder containing the test suites (you can copy the lines from `__SAKULI_HOME__/conf/sakuli-default.properties`):

`__INST_DIR__/example_test_suites/sakuli.properties`:

  * `sakuli.forwarder.check_mk.enabled=true`
  * `sakuli.forwarder.check_mk.spooldir=C:\\Program Files (x86)\\check_mk\\spool` - Path to the spool folder as defined above. On Windows, the backslashes have to be escaped with '\'. Check_MK is expecting the result files from Sakuli here. Default value: `/var/lib/check_mk_agent/spool` (Linux) / `(installation_path)\\spool` (Windows).
  * `sakuli.forwarder.check_mk.freshness` - Defines the maximal age in seconds in which the result is still valid. If the last modification of the result file is older than this property, the result file will be ignored. The check_mk service will turn into UNKNOWN. Default value: `600`.
  * `sakuli.forwarder.check_mk.spoolfile_prefix` - Defines the result file prefix. It can be used to change the default naming convention for the Check_MK output files. Default value: `sakuli_suite_`.
  * `sakuli.forwarder.check_mk.service_description` - Defines the service description, which is used within the check result. Default value: `${testsuite.id}`. If you want to use a PNP graph template, the service name must begin with "sakuli_". Only then the generic PNP template of check_mk for local (=passive) checks will be able to determine "sakuli.php" as a valid template for this check result. For more information see [here](https://mathias-kettner.de/checkmk_localchecks.html#PNP%20Templates%20for%20local%20checks).

### Output format template

With the implementation of the Check_MK forwarder a new [Jtwig](http://jtwig.org/) templating mechanism has been introduced in Sakuli, which decouples the output format from the Sakuli binary. This which allows format adaptions to fulfill your needs at any time without installing a new version of Sakuli.

In near time, all other forwarder modules of Sakuli will support the templating feature.

Sakuli comes with default templates, which are placed in ''$INSTALL_DIR/config/templates''. The default Check_MK templates can be found in a subdirectory check_mk.

For further information how the default template directory can be changed or how the forwarder templates can be customized please refer to [Using Jtwig templates in Sakuli](forwarder-templates.md).
