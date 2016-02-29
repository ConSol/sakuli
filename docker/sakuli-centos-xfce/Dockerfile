# This Dockerfile is used to build an sakuli image based on Centos

FROM consol/centos-xfce-vnc
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-02-19

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 0.9.3-SNAPSHOT
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce

### Install Sakuli
##Install some native libaries
RUN yum -y install \
    # opencv
    opencv \
    # tesseract
    http://repo.iotti.biz/CentOS/7/x86_64/leptonica-1.71-2.el7.lux.1.x86_64.rpm \
    http://repo.iotti.biz/CentOS/7/x86_64/tesseract-3.03-0.2.rc1.el7.lux.1.x86_64.rpm \
    # wmctrl
    http://mirror.symnds.com/distributions/gf/el/7/gf/x86_64/wmctrl-1.07-17.gf.el7.x86_64.rpm \
    # xdotool
    http://li.nux.ro/download/nux/dextop/el7/x86_64/xdotool-2.20110530.1-7.el7.nux.x86_64.rpm \
    http://li.nux.ro/download/nux/dextop/el7/x86_64/libxdo-2.20110530.1-7.el7.nux.x86_64.rpm
## Install sakuli
ADD scripts/auto-install.xml /tmp/
RUN wget --directory-prefix=/tmp $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION-installer.jar \
    && java -jar /tmp/sakuli-v$SAKULI_VERSION-installer.jar /tmp/auto-install.xml \
    && rm -f sakuli*.jar auto-install.xml

### Install apps for testing
RUN yum -y install gedit gnome-calculator

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
