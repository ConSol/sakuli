# This Dockerfile is used to build a sakuli image based on CentOS

FROM consol/centos-icewm-vnc:1.3.0

MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2018-06-27

LABEL io.k8s.description="Sakuli headless testing container with Xfce window manager, firefox and chromium" \
      io.k8s.display-name="Sakuli testing container based on Centos and Xfce" \
      io.openshift.expose-services="6901:http,5901:xvnc" \
      io.openshift.tags="sakuli, centos, xfce" \
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
ADD ./sakuli-client/src/centos/install/ $INST_SCRIPTS/
RUN find $INST_SCRIPTS -name '*.sh' -exec chmod a+x {} +

### Install needed packages
RUN $INST_SCRIPTS/example_apps.sh
RUN $INST_SCRIPTS/screenshot_tool.sh
RUN $INST_SCRIPTS/native_screen_control_libs.sh
RUN $INST_SCRIPTS/java_jre.sh
RUN $INST_SCRIPTS/java_jce_test/jce_test.sh

### Install Sakuli
ARG SAKULI_VERSION=1.2.0
# SAKULI_UMASK: Testsuite folder default permissions after text execution
# SAKULI_EXAMPLE_TEST_SUITE: Define the example which will be used as default test
# SAKULI_TEST_SUITE: Define Sakuli default startup testsuite
ENV SAKULI_ROOT=$HOME/sakuli \
    SAKULI_HOME=$HOME/sakuli/sakuli-v$SAKULI_VERSION \
    SAKULI_UMASK=0000 \
    SAKULI_EXAMPLE_TEST_SUITE=example_icewm \
    SAKULI_TEST_SUITE=$HOME/sakuli/test
WORKDIR $SAKULI_ROOT
# Install the $SAKULI_VERSION and create the example testsuite under $SAKULI_TEST_SUITE
RUN $INST_SCRIPTS/sakuli.sh

### configure startup
ADD ./sakuli-client/src/common/scripts $STARTUPDIR
RUN $INST_SCRIPTS/set_user_permission.sh $STARTUPDIR
# use headless user for startup
USER 1000

### Sakuli startup script
# no parameters:
# - run the suite defined by $SAKULI_TEST_SUITE, if set
# parameters:
# - run a Sakuli test suite like the example_xfce case via:
#   docker run consol/sakuli-centos-xfce run /sakuli/example_test_suites/example_xfce
# - help:
#   docker run consol/sakuli-centos-xfce -help
# - start a bash (or any other command):
#   docker run -it consol/sakuli-centos-xfce bash
ENTRYPOINT ["/dockerstartup/startup.sh"]
