# Usage of Sakuli Docker Containers

## Architecture of the Container
Each different container-image source file in the directory [./docker](https://github.com/ConSol/sakuli/tree/master/docker) contains a ready-to-use image with a headless VNC server and  already installed Sakuli.
The different docker images can be found at [DockerHub]():

* [consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/)
* [consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/)

Preinstalled components of the docker images:

* UI session, e.g. `Xfce`
* VNC-Server (default VNC port `5901`)
* [noVNC](https://github.com/kanaka/noVNC) - HTML5 VNC client (default http port `6901`)
* Java JRE 8
* Firefox + Java Plugin
* Chrome (Java-Plugin is no longer supported)
* Sakuli in the latest version

The running containers are accessible as follows:

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
The value of the environment variable `SAKULI_TEST_SUITE` defines which test suite will be run when the `docker run` command is called. Per default a Sakuli container executes the test suite "[example_xfce](https://github.com/ConSol/sakuli/tree/master/example_test_suites/example_xfce)" which have been already added to the docker image at the path `/root/sakuli/example_test_suites/example_xfce`.

To execute your own Sakuli test suite in the container you have more then one option. In the following examples we will mount and start a test suite called `suite_1` placed at the path `/home/myuser/my-sakuli-testsuites`:

#### 1) Mount your test suite and modify the `CMD` arguments

Simply mount your local test suite folder to the container and override default `CMD` arguments:

    docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites" consol/sakuli-ubuntu-xfce '--run /my-sakuli-testsuites/suite_1'

A more elegant way is the usage of [Docker Compose](https://docs.docker.com/compose/). In the case of this example the `docker-compose.yml` would look like:

    sakuli-example-ubuntu:
      image: consol/sakuli-ubuntu-xfce
      volumes:
      # mount all suites
      - /home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites
      # define Sakuli test suite and use chrome
      command: "'--run /my-sakuli-testsuites/suite_1'"
      ports:
      #vnc
      - 5901:5901
      #noVNC HTML client
      - 6901:6901

To start the container call `docker-compose up` on the command line. With the command `docker-compose rm -f` you can remove all currently stopped and running containers, which defined in the `docker-compose.yml`. Otherwise, if `docker-compose up` will called again, the test execution will reattach the instance and the start the test execution again in the same container instance.

#### 2) Mount your test suite and modify the environment variable `SAKULI_TEST_SUITE`
Simply mount your local test suite folder to the container and override the environment variable `SAKULI_TEST_SUITE`:

    docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites" -e "SAKULI_TEST_SUITE=/my-sakuli-testsuites/suite_1" consol/sakuli-ubuntu-xfce

In the case of this example the `docker-compose.yml` would look like:

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

### Use `sakuli.sh` command line parameters
Since `v0.9.2` it is possible to use the `sakuli.sh` command line parameters directly in the docker run command. Like for example to execute your test suite in the chrome browser modify your `docker run` command like:

    docker run consol/sakuli-ubuntu-xfce '--run $SAKULI_TEST_SUITE --browser chrome'

To use this option in the `docker-compose.yml` modify it like follow:

    sakuli-example-ubuntu:
      image: consol/sakuli-ubuntu-xfce
      # use chrome
      command: "'--run $SAKULI_TEST_SUITE --browser chrome'"

To get all possible command line parameters call `docker run consol/sakuli-ubuntu-xfce --help`.

### Override the VNC environment variables
The following VNC environment variables can be overwritten at the `docker run` phase to customize your desktop environment inside the container:
* `VNC_COL_DEPTH`, default: `24`
* `VNC_RESOLUTION`, default: `1280x1024`
* `VNC_PW`, default: `sakuli`

For example, the variable `VNC_PW` could be set like this:

    docker run -it -p 5901:5901 -p 6901:6901 -e "VNC_PW=my-new-password" consol/sakuli-ubuntu-xfce


## Further Information
Further Information about the usage of Sakuli docker containers can be found at:

* Presentation **[Containerized End-2-End-Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
* Example project on GitHub **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**.
