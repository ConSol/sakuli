![sakuli-logo](pics/sakuli_logo_small.png)

# Documentation

Our documentation is also available on **[Read the Docs](http://sakuli.readthedocs.io)**.
* Stable version (latest tagged `vX.X.X` documentation) [![Documentation Status](https://readthedocs.io/projects/sakuli/badge/?version=stable)](http://sakuli.readthedocs.io/en/stable/)
* Latest version (documentation on `master` branch, with corrections) [![Documentation Status](https://readthedocs.io/projects/sakuli/badge/?version=latest)](http://sakuli.readthedocs.io/en/latest/)
* Dev version (current development version on `dev` branch) [![Documentation Status](https://readthedocs.io/projects/sakuli/badge/?version=dev)](http://sakuli.readthedocs.io/en/dev/)

## Usage
### Basic setup

* [Sakuli client installation](installation-client.md)
* [Sakuli JavaDSL setup](java-DSL.md)
* [Usage of Sakuli Docker Images](docker-images.md)
* Forwarding Sakuli results to other systems:
    * [Open Monitoring Distribution (OMD)](installation-omd.md)
    * [Gearman Daemon](forwarder-gearman.md)
    * [SQL Database](forwarder-database.md)
    * [Icinga2 REST API](forwarder-icinga2api.md)
    * [Check_MK](forwarder-checkmk.md)
* Tutorial: [First steps](first-steps.md)
* Example projects on GitHub [ConSol/sakuli-examples](https://github.com/ConSol/sakuli-examples)

### Sakuli API

Sakuli provides methods of three different types:

* JS methods of **[Sahi](http://sahi.co.in/w/all-apis)**, which can be used 100% *natively*
* JS methods which encapsulate a subset of the **Sikuli** Java API
* JS methods of **Sakuli** framework itself (testcase stuff, exception handling, …)  

For documentation see

* [Sahi-API](http://sahi.co.in/w/all-apis)
* [TestCase](sakuli-api.md#TestCase)
* [Application](sakuli-api.md#Application)
* [Environment](sakuli-api.md#Environment)
* [Region](sakuli-api.md#Region)
* [Keys](sakuli-api.md#Key)
* [MouseButton](sakuli-api.md#MouseButton)
* [Logger](sakuli-api.md#Logger)

### Sakuli Java API

In addition to the JavaScript APIs Sakuli provides a Java DSL for writing tests in Java. This Sakuli Java API enables users to write
Sakuli tests in pure Java unit tests using JUnit or TestNG. The good news about that is that you are still able to access any
native application UI with screen related actions. The API is documented in [Java DSL](java-dsl.md).

## Advanced topics

* **Containerized Sakuli checks with [Docker](https://www.docker.com/)**
  * [Usage Sakuli Docker Images](docker-images.md)
  * [Usage Sakuli in OpenShift](openshift.md)
  * [Usage Sakuli in Kubernetes](kubernetes.md)
  * [Usage OMD-Labs Docker images with Sakuli support](omd-labs-sakuli.md)
  * Presentation [Containerized End-2-End-Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)
  * Example project on GitHub [toschneck/sakuli-example-bakery-testing](https://github.com/toschneck/sakuli-example-bakery-testing)
* **Headless** Sakuli checks
  * [on Windows](headless-windows.md) -- in progress
  * [on Linux](headless-linux.md)
* [additional Sakuli Settings](additional-settings.md)
* **Troubleshooting** and **Tuning**
  * [Sakuli Client](troubleshooting-tuning-sakuli-client.md)
  * [OMD/Nagios](troubleshooting-omd.md)

## How to contribute

* [Installation instructions for developers](development/installation-developers.md)
* [How to prepare a new release](development/how-to-release.md)
* [Maven Build Aspects](development/maven-aspects.md)
