# Usage Sakuli Docker Containers

## Architecture of the container
All different image source files in the directory [./docker](https://github.com/ConSol/sakuli/tree/master/docker) contains a ready-to-use images with headless VNC environments and Sakuli installation. Under [DockerHub]() you will find the following images:
* [consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/)
* [consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/)

The above images have installed in detail the following components:

* UI session like e.g. `Xfce`
* VNC-Server with default VNC port `5901`
* [noVNC](https://github.com/kanaka/noVNC) - HTML5 VNC client
* Java JRE 8
* Firefox + Java Plugin
* Chrome (Java-Plugin is not longer supported)
* Sakuli in the latest version

All the provided containers provides the following interfaces to follow the graphical output during the test execution:

* connect via __VNC viewer `localhost:5901`__, default password: `sakuli`
* connect via __noVNC HTML5 client__: [http://localhost:6901/vnc_auto.html?password=sakuli]()

## Current provided OS & UI containers:
* **[consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/)** - __Centos7 with `Xfce4` UI session:__

  Run command with mapping to local port `5901` and `6901`:

      ### start the docker container
      # vnc-port:    5901  => connect via VNC viewer localhost:5901
      # webVNC-port: 6901  => connect via URL: http://localhost:6901/vnc_auto.html?password=sakuli

      docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce

  Build image from scratch:

      git clone https://github.com/ConSol/sakuli.git
      docker build -t consol/sakuli-centos-xfce docker/sakuli-centos-xfce


* **[consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/)** - __Ubuntu 14.04 with `Xfce4` UI session:__

  Run command with mapping to local port `5901` and `6901`:

      ### start the docker container
      # vnc-port:    5901  => connect via VNC viewer localhost:5901
      # webVNC-port: 6901  => connect via URL: http://localhost:6901/vnc_auto.html?password=sakuli

      docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-ubuntu-xfce

  Build image from scratch:

      git clone https://github.com/ConSol/sakuli.git
      docker build -t consol/sakuli-centos-xfce docker/sakuli-ubuntu-xfce

## Hints
### Configure and mount a Sakuli testsuite
The value of the environment variable `SAKULI_TEST_SUITE` defines which test suite should be executed at the startup of the `docker run` command. Per default the Sakuli container execute in our example the test suite "[example_xfce](https://github.com/ConSol/sakuli/tree/master/example_test_suites/example_xfce)" which already added inside of the container under the path `/root/sakuli/example_test_suites/example_xfce`.

To execute your own Sakuli test suite, you can simple mount your local test suite folder to the container and override the environment variable `SAKULI_TEST_SUITE`. In the following example we will mount and start my test suite `suite_1` under the path `/home/myuser/my-sakuli-testsuites`:

    docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites" -e "SAKULI_TEST_SUITE=/my-sakuli-testsuites/suite_1" consol/sakuli-ubuntu-xfce

A more elegant way would be to use [Docker Compose](https://docs.docker.com/compose/). For the above mentioned example the `docker-compose.yml` would look like follow:

    sakuli-example-ubuntu:
      image: consol/sakuli-ubuntu-xfce
      volumes:
      # mount all suites
      - /home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites
      environment:
      # set Sakuli test suite
      - SAKULI_TEST_SUITE=/my-sakuli-testsuites/suite_1
      ports:
      #vnc
      - 5901:5901
      #noVNC HTML client
      - 6901:6901

To start the container execution run `docker-compose rm -f && docker-compose up`. The command ` docker-compose rm -f` will ensure that currently stopped container won't be reattached. This is necessary to startup the containers correctly.


### Override the VNC environment variables
The following VNC environment variables can be overridden at the `docker run` phase to customize your desktop environment inside of the container:
* `VNC_COL_DEPTH`, default: `24`
* `VNC_RESOLUTION`, default: `1280x1024`
* `VNC_PW`, default: `sakuli`

Like for example simple override the value of the environment variable `VNC_PW` with the command:

    docker run -it -p 5901:5901 -p 6901:6901 -e "VNC_PW=my-new-password" consol/sakuli-ubuntu-xfce


## Further Information
Further Information about the usage of the Sakuli docker containers you find under:
* Presentation **[Containerized End-2-End-Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
* Example project on GitHub **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**.
