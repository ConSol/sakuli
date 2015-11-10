# Usage of Sakuli Docker Containers

Docker containers allow you to run a Sakuli test in an isolated environment, called "container", which is always started off from the same base image. This ensures that tests always run under equal conditions.

## Container OS types
The repository's subfolder [./docker](https://github.com/ConSol/sakuli/tree/master/docker) contains all source files Sakuli docker images are made of. Currently we provide images on  [DockerHub](https://hub.docker.com) for:
* CentOS 7 ([consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/))
* Ubuntu 14.04 LTS ([consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/))

## Architecture of Sakuli containers

Each Sakuli docker image is installed with the following components:

* Desktop environment [**Xfce4**](http://www.xfce.org)
* VNC-Server (default VNC port `5901`)
* [**noVNC**](https://github.com/kanaka/noVNC) - HTML5 VNC client (default http port `6901`)
* Java JRE 8
* Browsers:
  * Mozilla Firefox + Java Plugin
  * Google Chrome (Java-Plugin is no longer supported)
* [**Sahi OS**](http://sahi.co.in)
* [**Sakuli**](https://github.com/ConSol/sakuli) in the latest stable version

The running containers are accessible as follows:

* __VNC viewer__:
  *  __`DOCKER_HOST:5901`__
  * default password: `sakuli`
* __noVNC HTML5 client__:
  * [http://localhost:6901/vnc_auto.html?password=sakuli]()

The default password for VNC can be overridden by setting the environment variable `VNC_PW` on the command line, e.g.:

      docker run -it -e "VNC_PW=my-new-password" ... ...

## Get Sakuli docker images

The following example command pulls the CentOS7 image from DockerHub:

    docker pull consol/sakuli-centos-xfce

Alternatively, you can build this image from the sources:

    git clone https://github.com/ConSol/sakuli.git
    docker build -t consol/sakuli-centos-xfce docker/sakuli-centos-xfce .

## Start/test a Sakuli container

Once you have pulled/built the image, you can start a container on top of it which binds port 5901/tcp and 6901/tcp to localhost:

      docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce

The container will execute a small headless self-test and exit afterwards. Read on to learn how to execute your own test within this container.  

## Integrate custom test suites in a Sakuli container

There are three important lines in the *Dockerfile* of each Sakuli image which define what has to be done on a container start:

    ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce
    ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
    CMD ["--run $SAKULI_TEST_SUITE"]

* `ENTRYPOINT` is the command which is executed once the container is started with `docker run`.
* `CMD` is the default argument for `ENTRYPOINT`, that is, to run a test suite set by a variable.
* `ENV SAKULI_TEST_SUITE` is set to the path of a test suite which has to run when the container starts. By default, this is set to the built-in folder `/root/sakuli/example_test_suites/example_xfce`.

There is more than one way to integrate a custom testsuite in a container, discussed in the following and assuming that you want to integrate a suite called `suite_1` located on your host at the path `/home/myuser/my-sakuli-testsuites`:

### 1) Mount an external suite folder and modify `CMD`

Simply mount a folder on your host into the container and override `CMD` from Dockerfile (=argument for `ENTRYPOINT`).

You can do this in two ways:

#### 1.1) using the command line

Set all parameters on the command line:

    docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites" consol/sakuli-centos-xfce '--run /my-sakuli-testsuites/suite_1'

#### 1.2) using docker-compose
A more elegant way is to pack all parameters into a [Docker Compose](https://docs.docker.com/compose/) file. Create `docker-compose.yml`:

    sakuli-example-ubuntu:
      image: consol/sakuli-centos-xfce
      ports:
      - 5901:5901
      - 6901:6901
      volumes:
      - /home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites
      command: "'--run /my-sakuli-testsuites/suite_1'"

When executed in the same directory as `docker-compose.yml`, a simple `docker-compose up` will bring up all containers.
(Intentionally, `docker-compose` is made to bring up environments with *multiple* containers which are linked together; but even with one container it eases the parameter handling.)

`docker-compose rm -f` in contrast removes all currently stopped and running containers, which defined in the `docker-compose.yml`. Otherwise, if `docker-compose up` will called again, the test execution will reattach the instance and the start the test execution again in the same container instance.

### 2) Mount your test suite and modify the environment variable `SAKULI_TEST_SUITE`
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
