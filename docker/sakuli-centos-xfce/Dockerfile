# This Dockerfile is used to build an sakuli image based on Centos

FROM consol/centos-xfce-vnc:1.0.2
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-10-28

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ARG SAKULI_VERSION=1.0.2-lhm
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce

### Install Sakuli
##Install some native libaries
RUN yum -y install unzip \
    # opencv
    opencv \
    # tesseract
    leptonica tesseract tesseract-langpack-deu \
    # wmctrl  (checked by: https://www.virustotal.com/de/url/394da2ac7e9bbbf5d10a3f304cc913459fc38f66019a4c3ba0fd4afd8f48e64f/analysis/1465466765/)
    $SAKULI_DOWNLOAD_URL/3rd-party/rpm/wmctrl-1.07-17.gf.el7.x86_64.rpm \
    # some missing ui controll tools
    xdotool redhat-lsb-core psmisc \
   && yum clean all
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

# startup script
ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
# arguments for the sakuli_startup.sh script
CMD ["run $SAKULI_TEST_SUITE"]
