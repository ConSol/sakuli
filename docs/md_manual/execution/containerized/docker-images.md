# Usage of Sakuli Docker Images

Docker allows you to run a Sakuli test in an isolated environment, called "container", which is always started off from the same base image. This ensures that tests always run under equal conditions.

## Image OS types
The repository's subfolder [./docker](https://github.com/ConSol/sakuli/tree/master/docker) contains all source files Sakuli docker images are made of. Currently we provide images on  [DockerHub](https://hub.docker.com) for:
* CentOS 7 ([consol/sakuli-centos-xfce](https://hub.docker.com/r/consol/sakuli-centos-xfce/), [consol/sakuli-centos-xfce-java](https://hub.docker.com/r/consol/sakuli-centos-xfce-java/))
* Ubuntu 14.04 LTS ([consol/sakuli-ubuntu-xfce](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce/),[consol/sakuli-ubuntu-xfce-java](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce-java/))

## Image tags
The build process on DockerHub is triggered by Github hooks; that means that you are always getting the current version for the two branches

* **master** -> image tag "**latest**": contains the latest stable release of Sakuli
* **dev** -> image tag "**dev**": contains the latest snapshot version of Sakuli

## Architecture of Sakuli containers

Each Sakuli docker image is installed with the following components:

* Desktop environment ([**Xfce4**](http://www.xfce.org))
* VNC-Server (default VNC port `5901`)
* [**noVNC**](https://github.com/kanaka/noVNC) - HTML5 VNC client (default http port `6901`)
* Java JRE 8
* Browsers:
  * Mozilla Firefox + Java Plugin
  * Google Chrome (Java-Plugin is no longer supported)
* [**Sahi OS 5**](http://sahi.co.in)
* [**Sakuli**](https://github.com/ConSol/sakuli) in the latest stable version

The running containers are accessible with VNC (default password: *sakuli*) by:

* __VNC viewer__:
  *  __`DOCKER_HOST:5901`__
* __noVNC HTML5 client__:
  * [http://localhost:6901/vnc_auto.html?password=sakuli]()


## Get Sakuli Docker Images

The following example command pulls the CentOS7 image from [DockerHub](https://hub.docker.com/r/consol/sakuli-centos-xfce/):

    docker pull consol/sakuli-centos-xfce

Alternatively, you can build this image from the sources:

    git clone https://github.com/ConSol/sakuli.git
    docker build -t consol/sakuli-centos-xfce docker/sakuli-centos-xfce .

## Start/test a Sakuli container

Once you have pulled/built the image, you can start a container on top of it which binds port 5901/tcp and 6901/tcp to localhost (on native docker installations; $DOCKER_IP on boot2docker):

      # default tag "latest" = Sakuli stable
      docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce
      # tag "dev" = Sakuli Snapshot version of dev branch
      docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce:dev

The container will execute a small headless self-test and exit afterwards. Read on to learn how to execute your own JavaScript or Java based tests within this containers.  

## 1) Run your test suite in a Sakuli container - JavaScript based test

There are three important lines in the *Dockerfile* of each Sakuli image which define what has to be done on a container start:

    ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce
    ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
    CMD ["run $SAKULI_TEST_SUITE"]

* `ENTRYPOINT` is the command which is executed once the container is started with `docker run`.
* `CMD` is the default argument for `ENTRYPOINT`, that is, to run a test suite set by a variable.
* `ENV SAKULI_TEST_SUITE` is set to the path of a test suite which has to run when the container starts. By default, this is set to the built-in folder `/root/sakuli/example_test_suites/example_xfce`.

There is more than one way to integrate a custom testsuite in a container, discussed in the following. Assume you want to run a suite called `suite_1` located on your host at the path `/home/myuser/my-sakuli-testsuites` - use one of the following ways:

### 1.1) `run` command

Mount the suite folder on your host into the container and override `CMD` from Dockerfile (=argument for `ENTRYPOINT`) with custom parameters for the Sakuli starter `sakuli`.  In this way you can also give further parameters to Sakuli e.g. to use another browser (`-browser chrome`).

    # running tests in chrome
    ~$ docker run -it -p 5901:5901 -p 6901:6901 consol/sakuli-centos-xfce 'run "$SAKULI_TEST_SUITE" -browser chrome'   

 To get all possible command line parameters call `docker run consol/sakuli-ubuntu-xfce -help`.


CMD can be overrideen in two ways:

#### 1.1.1) Using the command line

    ~$ docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites" consol/sakuli-centos-xfce 'run /my-sakuli-testsuites/suite_1'
This command will
  * mount the test suites folder to `/my-sakuli-testsuites` within the container
  * execute the suite `suite_1`

#### 1.1.2) Using docker-compose
A more elegant way is to pack all parameters into a [Docker Compose](https://docs.docker.com/compose/) file. Create `docker-compose.yml`:

    sakuli-example-ubuntu:
      image: consol/sakuli-centos-xfce
      ports:
      - 5901:5901
      - 6901:6901
      volumes:
      - /home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites
      command: "'run /my-sakuli-testsuites/suite_1'"

When executed in the same directory as `docker-compose.yml`, a simple `docker-compose up` will bring up all containers.
(Intentionally, `docker-compose` is made to bring up environments with *multiple* containers which are linked together; but even with one container it eases the parameter handling.)

`docker-compose rm -f` in contrast removes all currently stopped and running containers, which defined in the `docker-compose.yml`. Otherwise, if `docker-compose up` will called again, the test execution will reattach the instance and the start the test execution again in the same container instance.

### 1.2) Environment variable `SAKULI_TEST_SUITE`
Mount a folder on your host into the container and override the environment variable `SAKULI_TEST_SUITE`.

#### 1.2.1) Using the command line

    ~$ docker run -it -p 5901:5901 -p 6901:6901   \\
         -v "/home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites"   \\
         -e "SAKULI_TEST_SUITE=/my-sakuli-testsuites/suite_1"   \\
         consol/sakuli-ubuntu-xfce

#### 1.2.2) Using docker-compose
Similar to [#12-using-docker-compose](docker-compose in 1.2), the file `docker-compose.yml` would look like this:

    sakuli-example-ubuntu:
      image: consol/sakuli-ubuntu-xfce
      ports:
      - 5901:5901
      - 6901:6901
      volumes:
      - /home/myuser/my-sakuli-testsuites:/my-sakuli-testsuites
      environment:
      - SAKULI_TEST_SUITE=/my-sakuli-testsuites/suite_1

## 2) Run your test suite in a Sakuli container - Java based test

Also for Sakuli test writen in Java and executed through [Maven](https://maven.apache.org), we provide to preconfigured docker images: [consol/sakuli-centos-xfce-java](https://hub.docker.com/r/consol/sakuli-centos-xfce-java/), [consol/sakuli-ubuntu-xfce-java](https://hub.docker.com/r/consol/sakuli-ubuntu-xfce-java/). More information about how to write a Java based Sakuli test, see [Sakuli JavaDSL setup](../maven/java-DSL.md). Now take a look at the important lines in the *Dockerfile* which define how the container will start:

    WORKDIR /opt/maven
    ENTRYPOINT ["/root/scripts/start_hook.sh"]
    CMD ["mvn clean test"]

* `WORKDIR` is set to the path, where the maven build will be executed. By default, this is set to the built-in example folder `/opt/maven`.
* `ENTRYPOINT` is the command which is executed once the container is started with `docker run`.
* `CMD` is the default argument for `ENTRYPOINT`, what will trigger the Maven build, which contains the Java based Sakuli tests.

Assume you want to run the Sakuli end-2-end test from your Maven project located at the path `/home/myuser/my-sakuli-maven-project` you can executed the Maven build in the inside of the Sakuli container like follow:


#### 2.1) Using the command line

    ~$ docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-maven-project:/opt/maven" consol/sakuli-ubuntu-xfce-java

This command will
  * mount the test suites folder to `/home/myuser/my-sakuli-maven-project` within the container
  * execute the maven build with default command `mvn clean test`

If you want to for example also build youre maven artifacts over `mvn install` override the default command like follow:

    ~$ docker run -it -p 5901:5901 -p 6901:6901 -v "/home/myuser/my-sakuli-maven-project:/opt/maven" consol/sakuli-ubuntu-xfce-java 'mvn clean install'

#### 2.2) Using docker-compose
A more elegant way is to pack all parameters into a [Docker Compose](https://docs.docker.com/compose/) file. Create `docker-compose.yml`:

    sakuli-example-ubuntu:
      image: consol/sakuli-ubuntu-xfce-java
      ports:
      - 5901:5901
      - 6901:6901
      volumes:
      - /home/myuser/my-sakuli-maven-project:/opt/maven

When executed in the same directory as `docker-compose.yml`, a simple `docker-compose up` will bring up all containers.
(Intentionally, `docker-compose` is made to bring up environments with *multiple* containers which are linked together; but even with one container it eases the parameter handling.)

`docker-compose rm -f` in contrast removes all currently stopped and running containers, which defined in the `docker-compose.yml`. Otherwise, if `docker-compose up` will called again, the test execution will reattach the instance and the start the test execution again in the same container instance.

Like above you can for example also override the default mvn command and use a additional persistent volume for caching the maven dependencies:

    version: '2'

    services:
      sakuli_java_test:
        image: consol/sakuli-ubuntu-xfce-java
        volumes:
        - /home/myuser/my-sakuli-maven-project:/opt/maven
        - data:/root/.m2
        network_mode: "bridge"
        ports:
        - 5911:5901
        - 6911:6901
        command: "'mvn clean install'"
        # to keep container running and login via `docker exec -it javaexample_sakuli_java_test_1 bash`
        # command: "'--tail-log'"

    volumes:
      data:
        driver: local

## 3) Override VNC environment variables
The following VNC environment variables can be overwritten at the `docker run` phase to customize your desktop environment inside the container:
* `VNC_COL_DEPTH`, default: `24`
* `VNC_RESOLUTION`, default: `1280x1024`
* `VNC_PW`, default: `sakuli`

For example, the password for VNC could be set like this:

    ~$ docker run -it -p 5901:5901 -p 6901:6901 -e "VNC_PW=my-new-password"  \\
         consol/sakuli-ubuntu-xfce


## 4) Further Information
Further information about the usage of Sakuli docker containers can be found at:

* Publications:
 * **[Containerized UI-Tests in Java with Sakuli and Docker](https://labs.consol.de/sakuli/development/2016/10/14/sakuli-java-dsl.html)**
* Presentations:
 * **[Containerized End-2-End-Testing - ContainerDays 2016 Hamburg](https://speakerdeck.com/toschneck/containerized-end-2-end-testing-containerdays-2016-in-hamburg)**
 * **[Containerized End-2-End-Testing - ConSol CM Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
* Example projects on GitHub:
 * **[ConSol/sakuli-examples](https://github.com/ConSol/sakuli-examples)**
 * **[toschneck/sakuli-example-bakery-testing](https://github.com/toschneck/sakuli-example-bakery-testing)**
 * **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**

# Contact
For questions or maybe some hints, feel free to contact us via **[sakuli@consol.de](mailto:sakuli@consol.de)** or open an [issue](https://github.com/ConSol/sakuli/issues/new).

The guys behind Sakuli:
<table>
<tr>
<td>
**ConSol Consulting & Solutions Software GmbH** <br/>
*Franziskanerstr. 38, D-81669 München* <br/>
*Tel. +49-89-45841-100, Fax +49-89-45841-111*<br/>
*Homepage: http://www.consol.de E-Mail: [info@consol.de](info@consol.de)*
</td>
</tr>
<table>
