# Check_MK Forwarder
To enable the Check_MK output from Sakuli, the property `sakuli.forwarder.check_mk.enabled` shall be set accordingly.
In addition following properties affecting the behaviour of the Check_MK forwarder are existing and can be modified: 
* `sakuli.forwarder.check_mk.spooldir` - Defines the directory, in which Check_MK is expecting the result files from Sakuli. Default value: `/var/lib/check_mk_agent/spool` (Linux) / `(installation_path)\spool` (Windows).
* `sakuli.forwarder.check_mk.freshness` - Defines the maximal age in seconds in which the result is still valid. If the last modification of the result file is older than this property, the result file will be ignored. Default value: `600`.
* `sakuli.forwarder.check_mk.spoolfile_prefix` - Defines the based result file name. It can be used to change the default naming convention for the Check_MK output files. Default value: `sakuli_suite_`.
* `sakuli.forwarder.check_mk.service_description` - Defines the service description, which is used within the check result. Default value: `${testsuite.id}`

An example configuration of the properties, could look like:

    sakuli.forwarder.check_mk.enabled=true
    sakuli.forwarder.check_mk.spooldir=/var/lib/check_mk_agent/spool
    sakuli.forwarder.check_mk.freshness=600
    sakuli.forwarder.check_mk.spoolfile_prefix=sakuli_suite_
    sakuli.forwarder.check_mk.service_description=My_Custom_Service

With the implementation of the Check_MK forwarder a new templating mechanism has been introduced in Sakuli. Based on different templates the output of the forwarder can be adapted to fulfill the customer needs. With the new concepts Sakuli doesn't need to be released, if the forwarder output has to be slightly modified or customized.
 
The templates are defining how the output of the Sakuli test suite for the Check_MK would look like. Currently only the Check_MK forwarder is supporting the new templating mechanism. Sakuli comes with default templates, which are placed within the templates directory under `config/templates`. The [default Check_MK templates](../src/common/src/main/resources/org/sakuli/common/config/templates/check_mk) can be found in a subdirectory `check_mk`.

For further information how the default template directory can be changed or how the forwarder templates can be customized please refer to [Using Jtwig templates in Sakuli](forwarder-templates.md).
