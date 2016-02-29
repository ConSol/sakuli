# This Dockerfile is used to build an sakuli image based on Ubuntu

FROM consol/ubuntu-xfce-vnc
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-02-28

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 0.9.3
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce

### Install Sakuli
##Install some native libaries
RUN apt-get update && apt-get -y install libcv2.4 libtesseract3 wmctrl xdotool psmisc
## Install sakuli
ADD scripts/auto-install.xml /tmp/
RUN wget --directory-prefix=/tmp $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION-installer.jar \
    && java -jar /tmp/sakuli-v$SAKULI_VERSION-installer.jar /tmp/auto-install.xml \
    && rm -f sakuli*.jar auto-install.xml

### Install apps for testing
RUN apt-get -y install gedit gnome-calculator

# xvnc server port, if $DISPLAY=:1 port will be 5901
EXPOSE 5901
# novnc web port
EXPOSE 6901

ADD scripts/sakuli_startup.sh /root/scripts/
RUN chmod +x /root/scripts/*.sh

# startup script
ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
# arguments for the sakuli_startup.sh script
CMD ["run $SAKULI_TEST_SUITE"]
