# This Dockerfile is used to build an sakuli image based on Ubuntu

FROM consol/ubuntu-xfce-vnc:dev
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-10-28

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ARG SAKULI_VERSION=1.1.0-SNAPSHOT
ENV SAKULI_HOME /sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /sakuli/example_test_suites/example_xfce

# xvnc server port, if $DISPLAY=:1 port will be 5901
EXPOSE 5901
# novnc web port, connect via URL: http://$HOST:6901/vnc_auto.html?password=...
EXPOSE 6901

# use root user for installation
USER root

### Install some basic native apps for testing
RUN apt-get update \
    && apt-get -y install --fix-missing gedit gnome-calculator shutter \
    && rm -rf /var/lib/apt

### Install Sakuli
##Install some native libaries
RUN apt-get update \
    && apt-get -y install --fix-missing libcv2.4 libtesseract3 wmctrl xdotool psmisc unzip \
    && rm -rf /var/lib/apt
## Install JAVA JCE policy (necessary for gearman encryption)
RUN mkdir -p /tmp/java-jce \
    && cd /tmp/java-jce \
    && wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip \
    && unzip *.zip \
    && for i in $(find /usr/lib/jvm -name "security"); do /bin/cp -v -f UnlimitedJCEPolicyJDK8/*.jar $i; done \
    && rm -rf /tmp/java-jce

## Install sakuli
ADD scripts/auto-install.xml /tmp/sakuli/
RUN wget --directory-prefix=/tmp/sakuli $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION-installer.jar \
    && java -jar /tmp/sakuli/sakuli-v$SAKULI_VERSION-installer.jar /tmp/sakuli/auto-install.xml \
    && rm -rf /tmp/sakuli
RUN sed -i -e 's/proxy_alert.disabled=.*/proxy_alert.disabled=true/' /sakuli/sahi/userdata/config/userdata.properties

ADD scripts/sakuli_startup.sh /sakuli/scripts/
RUN chmod +x /sakuli/scripts/*.sh
RUN chgrp -R 0 /sakuli && chmod -R g+rw /sakuli && find /sakuli -type d -exec chmod g+x {} +
## use headless user
USER 1984

### Sakuli startup script
# no parameters:
# - run the suite defined by $SAKULI_TEST_SUITE, if set
# paramters:
# - run a Sakuli test suite like the example_xfce case via:
#   docker run consol/sakuli-ubuntu-xfce run /sakuli/example_test_suites/example_xfce
# - help:
#   docker run consol/sakuli-ubuntu-xfce help
# - start a bash (or any other command):
#   docker run -it consol/sakuli-ubuntu-xfce bash
ENTRYPOINT ["/sakuli/scripts/sakuli_startup.sh"]
