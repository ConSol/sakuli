![sakuli-logo](pics/sakuli_logo_small.png)

# Documentation

Our documentation is also avaible on **[Read the Docs](http://sakuli.readthedocs.org)**.
* Stable version (latest tagged `vX.X.X` documentation) [![Documentation Status](https://readthedocs.org/projects/sakuli/badge/?version=stable)](http://sakuli.readthedocs.org/en/stable/)
* Latest verison (documentation on `master` branch, with corrections) [![Documentation Status](https://readthedocs.org/projects/sakuli/badge/?version=latest)](http://sakuli.readthedocs.org/en/latest/)
* Dev version (current development version on `dev` branch) [![Documentation Status](https://readthedocs.org/projects/sakuli/badge/?version=dev)](http://sakuli.readthedocs.org/en/dev/)

## Usage
### Basic setup

* [Sakuli client **installation**](installation-client.md)
* Submitting Sakuli results
  * [to OMD (Nagios/Icinga2/Shinken)](installation-omd.md)
    * [via **Gearman**](forwarder-gearman.md)
    * [via **Database**](forwarder-database.md)

### Advanced topics###

* **Containerized Sakuli checks with [Docker](https://www.docker.com/)**
  * **[Usage Sakuli Docker Containers](../docs/docker-containers.md)**
  * Presentation **[Containerized End-2-End-Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
  * Example project on GitHub **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**.
* **Headless** Sakuli checks
  * [on Windows](headless-windows.md) -- in progress
  * [on Linux](headless-linux.md)
* [**additional** Sakuli Settings](additional-settings.md)
* **Troubleshooting** and **Tuning**
  * [**Sakuli Client**](troubleshooting-tuning-sakuli-client.md)
  * [**OMD/Nagios**](troubleshooting-omd.md)

## Sakuli API

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

## How to contribute

* [Installation instructions for **developers**](development/installation-developers.md)
* [How to prepare a **new releas**e](development/how-to-release.md)
* [**Maven Build Aspects**](development/maven-aspects.md)
