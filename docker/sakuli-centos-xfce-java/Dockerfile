# This Dockerfile is used to run Sakuli Java testss based on CentOS

FROM consol/centos-xfce-vnc:1.0.2
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-10-28

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

### Install some basic libs and JDK8
RUN wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u112-b15/jdk-8u112-linux-x64.rpm" -O /root/jdk.rpm \
    && yum install -y /root/jdk.rpm unzip \
    && rm /root/jdk.rpm \
    && yum clean all -y
ENV JAVA_HOME /usr/java/jdk1.8.0_112
# Install JAVA JCE policy (necessary for gearman encryption)
RUN mkdir -p /tmp/java-jce \
    && cd /tmp/java-jce \
    && wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip \
    && unzip *.zip \
    && for i in $(find /usr/java/ -name "security"); do /bin/cp -v -f UnlimitedJCEPolicyJDK8/*.jar $i; done \
    && rm -rf /tmp/java-jce

### Install Maven
ARG MAVEN_VERSION=3.3.9
ENV MAVEN_HOME /root/apps/maven
RUN mkdir -p $MAVEN_HOME \
  && wget http://www-eu.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz -O- \
    | tar -xzC $MAVEN_HOME --strip-components=1 \
  && ln -s $MAVEN_HOME/bin/mvn /usr/bin/mvn

##### Install some native libaries for Sakuli
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

#### Download Sakuli-Java-Example
RUN mkdir -p /tmp/sakuli-example \
    && cd /tmp/sakuli-example \
    && wget https://github.com/ConSol/sakuli-examples/archive/master.zip \
    && unzip *.zip \
    && mv sakuli-examples-master/java-example /opt/maven \
    && rm -rf /tmp/sakuli-example

### Startup hook for `/opt/maven`
# Custom startup hook to correct the file permissions after running a test,
# so mount your maven based projekt under `/opt/maven`
ADD scripts/start_hook.sh /root/scripts/
RUN chmod +x /root/scripts/start_hook.sh
WORKDIR /opt/maven
ENTRYPOINT ["/root/scripts/start_hook.sh"]
CMD ["mvn clean test"]
