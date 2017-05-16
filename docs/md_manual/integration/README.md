TODO

What integration possibilities do we have?
* Monitoring Systems
* Continuous Integration Systems
* Weitere Integration möglich, contact us ;-)

 

### Forwarder

TODO Liste der Forwarder hinzufügen


Sakuli can hand over test result to "**Forwarder**", which can be currently **GearmanD** servers (such as Nagios monitoring systems with mod-gearman) and **JDBC databases**. If no forwarder is defined, a result summary is printed out in the end of a suite. 

![sakuli_forwarders](./images/sakuli-forwarder.png)

For the configuration of forwarders on the OMD server side, see [Forwarder in OMD](installation-omd.md#forwarders)

  * [Setting up Sakuli client to send results to the **Database**](forwarder-database.md#sakuli-configuration)
  * [Setting up Sakuli client to submit results to the **Gearman Forwarder**](forwarder-gearman.md#sakuli-configuration)
