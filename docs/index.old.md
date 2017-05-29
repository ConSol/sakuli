![sakuli-logo](images/sakuli_logo_small.png)


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
