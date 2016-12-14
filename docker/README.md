# Sakuli on Docker

## Sakuli client images:

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

Further Information about the usage of the Sakuli docker containers are available here:
* **[Usage Sakuli Docker Containers](../docs/docker-containers.md)**
* Presentation **[Containerized End-2-End-Testing](https://rawgit.com/toschneck/presentation/sakuli-testautomation-day/index.html#/)**
* Example project on GitHub **[ConSol/sakuli-example-testautomation-day](https://github.com/ConSol/sakuli-example-testautomation-day)**.

## OMD Labs images with Sakuli extension:

These containers provide a nightly build of OMD Labs (https://labs.consol.de/OMD/) with Sakuli extensions:

- Sakuli PNP template
- Sakuli  FIXME
