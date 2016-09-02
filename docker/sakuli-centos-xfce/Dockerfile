# This Dockerfile is used to build an sakuli image based on Centos

FROM consol/centos-xfce-vnc:1.0.1
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-06-09

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 1.1.0-SNAPSHOT
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce

### Install Sakuli
##Install some native libaries
RUN yum -y install \
    # opencv
    opencv \
    # tesseract
    leptonica tesseract tesseract-langpack-deu \
    # wmctrl  (checked by: https://www.virustotal.com/de/url/394da2ac7e9bbbf5d10a3f304cc913459fc38f66019a4c3ba0fd4afd8f48e64f/analysis/1465466765/)
    $SAKULI_DOWNLOAD_URL/3rd-party/rpm/wmctrl-1.07-17.gf.el7.x86_64.rpm \
    # xdotool
    xdotool
## Install sakuli
ADD scripts/auto-install.xml /tmp/
RUN wget --directory-prefix=/tmp $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION-installer.jar \
    && java -jar /tmp/sakuli-v$SAKULI_VERSION-installer.jar /tmp/auto-install.xml \
    && rm -f sakuli*.jar auto-install.xml
RUN sed -i -e 's/proxy_alert.disabled=.*/proxy_alert.disabled=true/' /root/sakuli/sahi/userdata/config/userdata.properties

### Install apps for testing
RUN yum -y install gedit gnome-calculator

### Install Shutter
RUN yum -y install epel-release \
    && rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro \
    && rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-1.el7.nux.noarch.rpm \
    && yum -y --enablerepo=nux-dextop install shutter

# xvnc server port, if $DISPLAY=:1 port will be 5901
EXPOSE 5901
# novnc web port
EXPOSE 6901

ADD scripts/sakuli_startup.sh /root/scripts/
RUN chmod +x /root/scripts/*.sh

### Sakuli startup script
# no parameters:
# - run the suite defined by $SAKULI_TEST_SUITE, if set
# paramters:
# - run a Sakuli test suite like the example_xfce case via:
#   docker run consol/sakuli-ubuntu-xfce run /root/sakuli/example_test_suites/example_xfce
# - help:
#   docker run consol/sakuli-ubuntu-xfce help
# - start a bash (or any other command):
#   docker run -it consol/sakuli-ubuntu-xfce bash
ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
