# Sakuli on Docker

## Sakuli client images:

### For JavaScript based tests

* [consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/)
  ```
  ### start the docker container
  # vnc-port:    5901  => connect via VNC viewer localhost:5901
  # webVNC-port: 6901  => connect via URL: http://localhost:6901/vnc_auto.html?password=sakuli

  docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce
  ```
* [consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/)
  ```
  ### start the docker container
  # vnc-port:    5901  => connect via VNC viewer localhost:5901
  # webVNC-port: 6901  => connect via URL: http://localhost:6901/vnc_auto.html?password=sakuli

  docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-ubuntu-xfce
  ```

### For Java based tests

* [consol/sakuli-ubuntu-xfce-java](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce-java/)
  ```
  ### start the docker container and start example maven project at `/opt/maven`
  # vnc-port:    5901  => connect via VNC viewer localhost:5901
  # webVNC-port: 6901  => connect via URL: http://localhost:6901/vnc_auto.html?password=sakuli

  docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-ubuntu-xfce-java
  ```

* [consol/sakuli-centos-xfce-java](https://hub.docker.com/r/consol/sakuli-centos-xfce-java/)
  ```
  ### start the docker container  and start example maven project at `/opt/maven`
  # vnc-port:    5901  => connect via VNC viewer localhost:5901
  # webVNC-port: 6901  => connect via URL: http://localhost:6901/vnc_auto.html?password=sakuli

  docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce-java
  ```

### More:
Further Information about the usage of the Sakuli docker containers are available here:

* **[Usage Sakuli Docker Images](../docs/docker-images.md)**
* Publications:
 * **[Containerized UI-Tests in Java with Sakuli and Docker](https://labs.consol.de/sakuli/development/2016/10/14/sakuli-java-dsl.html)**
* Presentations:
 * **[Containerized End-2-End-Testing - ContainerDays 2016 Hamburg](https://speakerdeck.com/toschneck/containerized-end-2-end-testing-containerdays-2016-in-hamburg)**
 * **[Containerized End-2-End-Testing - ConSol CM Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
* Example projects on GitHub:
 * **[ConSol/sakuli-examples](https://github.com/ConSol/sakuli-examples)**
 * **[toschneck/sakuli-example-bakery-testing](https://github.com/toschneck/sakuli-example-bakery-testing)**
 * **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**

## OMD Labs images with Sakuli extension:

These containers provide a nightly build of OMD Labs (https://labs.consol.de/OMD/) with Sakuli extensions:

- Sakuli PNP template
- Screenshot eventhandler which stores screenshots in the file system
- Thruk SSI to display screenshots in a lightbox
- check_mysql_health to check a Sakuli result database
- adapted version of gearman_proxy.pl to retrieve results in a separate result queue (e.g. for replacing message substrings)
- Nagios example config files

### Automated builds

Each "omd-labs-sakuli" image build gets triggered by webhook when a new "omd-labs" image was created.

* https://hub.docker.com/r/consol/omd-labs-centos-sakuli/builds/
* https://hub.docker.com/r/consol/omd-labs-debian-sakuli/builds/
* https://hub.docker.com/r/consol/omd-labs-ubuntu-sakuli/builds/

The image already contains a "demo" site.

### Usage

#### run the "demo" site

Run the "demo" site in OMD Labs Edition:

    # Centos 7
    docker run -p 8443:443 consol/omd-labs-centos-sakuli
    # Ubuntu 16.04
    docker run -p 8443:443 consol/omd-labs-ubuntu-sakuli
    # Debian 8
    docker run -p 8443:443 consol/omd-labs-debian-sakuli

Use the Makefile to work with *locally built* images:

    # run a local image
    make start
    # build a "local/" image without overwriting the consol/ image
    make build
    # start just the bash
    make bash

#### run a custom site

If you want to create a custom site, you have to build an own image of OMD-labs **and** OMD-labs-sakuli.

* see the documentation of https://github.com/ConSol/omd-labs-docker, section "run a custom site" how to build a local image of omd-labs.
* then clone this repository, `cd` into the folder containg the Dockerfile, e.g. `omd-labs-centos-sakuli`
* build a local image:
      SITENAME=mynewsite
      make build    
* run the image:
      docker run -p 8443:443 local/omd-labs-centos-sakuli

## Ansible drop-ins
You can also configure this OMD labs container with Ansible playbooks. See https://github.com/ConSol/omd-labs-docker for more information.
