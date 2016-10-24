# Currently available docker images:

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
Further Information about the usage of the Sakuli docker containers you find under:

* **[Usage Sakuli Docker Containers](../docs/docker-containers.md)**
* Publications:
 * **[Containerized UI-Tests in Java with Sakuli and Docker](https://labs.consol.de/sakuli/development/2016/10/14/sakuli-java-dsl.html)**
* Presentations:
 * **[Containerized End-2-End-Testing - ContainerDays 2016 Hamburg](https://speakerdeck.com/toschneck/containerized-end-2-end-testing-containerdays-2016-in-hamburg)**
 * **[Containerized End-2-End-Testing - ConSol CM Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
* Example projects on GitHub:
 * **[ConSol/sakuli-examples](https://github.com/ConSol/sakuli-examples)**
 * **[toschneck/sakuli-example-bakery-testing](https://github.com/toschneck/sakuli-example-bakery-testing)**
 * **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**
