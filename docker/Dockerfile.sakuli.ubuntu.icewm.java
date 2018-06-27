# This Dockerfile is used to build a sakuli image based on Ubuntu

FROM consol/ubuntu-icewm-vnc:1.3.0

MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2018-06-27

LABEL io.k8s.description="Sakuli headless testing container (maven java tests) with IceWM window manager, firefox and chromium" \
      io.k8s.display-name="Sakuli testing container (maven java tests) based on Ubuntu and IceWM" \
      io.openshift.expose-services="6901:http,5901:xvnc" \
      io.openshift.tags="sakuli, ubuntu, icewm, java, maven" \
      io.openshift.non-scalable=true

### Environment config
ENV VNC_PORT=5901 \
    NO_VNC_PORT=6901 \
    VNC_COL_DEPTH=24 \
    VNC_RESOLUTION=1280x1024 \
    VNC_PW=sakuli

## Connection ports for controlling the UI:
# VNC port:5901
# noVNC webport, connect via http://IP:6901/?password=sakuli
EXPOSE $VNC_PORT $NO_VNC_PORT

# use root user for installation
USER root
# $INST_SCRIPTS is already set in FROM image
ADD ./sakuli-client/src/common/install/ $INST_SCRIPTS/
ADD ./sakuli-client/src_java/common/install/ $INST_SCRIPTS/
ADD ./sakuli-client/src/ubuntu/install/ $INST_SCRIPTS/
ADD ./sakuli-client/src_java/ubuntu/install/ $INST_SCRIPTS/
RUN find $INST_SCRIPTS -name '*.sh' -exec chmod a+x {} +

### Install needed packages
RUN $INST_SCRIPTS/screenshot_tool.sh
RUN $INST_SCRIPTS/native_screen_control_libs.sh
RUN $INST_SCRIPTS/java_jre.sh
RUN $INST_SCRIPTS/java_jdk.sh
RUN $INST_SCRIPTS/java_jce_test/jce_test.sh

### Install Maven
ARG MAVEN_VERSION=3.3.9
ENV MAVEN_HOME $HOME/apps/maven
RUN $INST_SCRIPTS/maven.sh

### Install Sakuli
ARG SAKULI_VERSION=1.2.0
# SAKULI_UMASK: Testsuite folder default permissions after text execution
# SAKULI_TEST_SUITE Define Sakuli default startup testsuite
ENV SAKULI_UMASK=0000 SAKULI_TEST_SUITE=/opt/maven
#
WORKDIR $SAKULI_TEST_SUITE
# Install the $SAKULI_VERSION and create the example testsuite under $SAKULI_TEST_SUITE
RUN $INST_SCRIPTS/zip.sh
RUN $INST_SCRIPTS/sakuli.sh

### configure startup
ADD ./sakuli-client/src/common/scripts/ $STARTUPDIR
ADD ./sakuli-client/src_java/common/scripts/ $STARTUPDIR
RUN $INST_SCRIPTS/set_user_permission.sh $STARTUPDIR
# use headless user for startup
USER 1000

### Sakuli startup script
# no parameters:
# - run the suite defined by $SAKULI_TEST_SUITE via `mvn test`
# if set parameters:
# - run the suite via typical maven commands like `mvn install`
# - start a bash (or any other command):
#   docker run -it consol/sakuli-ubuntu-xfce-java bash
ENTRYPOINT ["/dockerstartup/startup.sh"]
