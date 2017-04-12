# OMD Labs Docker images with Sakuli extension:

These images provide a nightly build of OMD Labs (https://labs.consol.de/OMD/) with Sakuli extensions:

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

* see the documentation of https://github.com/ConSol/omd-labs-docker, section "run a custom site" how to build a local image of OMD-labs.
* then clone this repository, `cd` into the folder containg the Dockerfile, e.g. `omd-labs-centos-sakuli`
* build a local image:

    export NEW_SITENAME=mynewsite
    make build    


* run the image:

    docker run -p 8443:443 local/omd-labs-centos-sakuli


## Ansible drop-ins
You can also configure this OMD labs container with Ansible playbooks. See https://github.com/ConSol/omd-labs-docker for more information.
