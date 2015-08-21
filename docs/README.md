![sakuli-logo](pics/sakuli_logo_small.png) 

 

# Documentation
Our documentation is also avaible on **[Read the Docs](http://sakuli.readthedocs.org)**.
* Dev version (current development version on `dev-v0.4` branch) [![Documentation Status](https://readthedocs.org/projects/sakuli/badge/?version=dev-v0.4)](http://sakuli.readthedocs.org/en/dev-v0.4/)
 

## Usage
### Basic setup

* [Sakuli client **installation**](installation-client.md)
* Submitting Sakuli results 
  * [to OMD (Nagios/Icinga2/Shinken)](installation-omd.md)
    * [via **Gearman**](receiver-gearman.md)
    * [via **Database**](receiver-database.md)

### Advanced topics###

* **Headless** Sakuli checks
  * [on Windows](headless-windows.md) -- in progress
  * [on Ubuntu](headless-ubuntu.md) -- in progress
* [**additional** Sakuli Settings](additional-settings.md)
* **Troubleshooting** and **Tuning**
  * [**Sakuli Client**](troubleshooting-tuning-sakuli-client.md) 
  * [**OMD/Nagios**](troubleshooting-omd.md) 
  
## Sakuli API

* [Sahi API](sakuli-api.md#Sahi API)
* [TestCase](sakuli-api.md#TestCase)
* [Application](sakuli-api.md#Application)
* [Environment](sakuli-api.md#Environment)
* [Region](sakuli-api.md#Region)
* [Keys](sakuli-api.md#Key)

## How to contribute

* [Installation instructions for **developers**](development/installation-developers.md)
* [How to prepare a **new releas**e](development/how-to-release.md)
* [**Maven Build Aspects**](development/maven-aspects.md)
