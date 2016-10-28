# This Dockerfile is used to build an sakuli image based on Centos

FROM consol/centos-xfce-vnc:1.0.1
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-10-28

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ARG SAKULI_VERSION=1.1.0-SNAPSHOT
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce

### Install Sakuli
##Install some native libaries
RUN yum -y install unzip \
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
## Install JAVA JCE policy (necessary for gearman encryption)
RUN mkdir -p /tmp/java-jce \
    && cd /tmp/java-jce \
    && wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip \
    && unzip *.zip \
    && for i in $(find /usr/java/ -name "security"); do /bin/cp -v -f UnlimitedJCEPolicyJDK8/*.jar $i; done \
    && rm -rf /tmp/java-jce

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

# TODO: remove when https://github.com/ConSol/docker-headless-vnc-container/issues/4 fixed and `1.0.2` released
ENV CHROMIUM_VERSION 53.0.2785.116-1
RUN yum erase -y google-chrome \
    && yum install -y http://install.linux.ncsu.edu/pub/yum/itecs/public/chromium/7/x86_64/chromium-$CHROMIUM_VERSION.el7.centos.x86_64.rpm \
    && yum clean all \
    && ln -s /usr/bin/chromium-browser /usr/bin/google-chrome
# fix to start chromium in a Docker container, see https://github.com/ConSol/docker-headless-vnc-container/issues/2
RUN sed -i -e 's/exec $CMD_PREFIX "$HERE\/chrome"/exec $CMD_PREFIX "$HERE\/chrome" $CHROMIUM_FLAGS/' /opt/chromium/chrome-wrapper
ENV CHROMIUM_FLAGS "--no-sandbox --start-maximized --user-data-dir"

# startup script
ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
# arguments for the sakuli_startup.sh script
CMD ["run $SAKULI_TEST_SUITE"]
