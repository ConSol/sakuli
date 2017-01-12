# This Dockerfile is used to run Sakuli Java testss based on Ubuntu OS

FROM consol/ubuntu-xfce-vnc:1.0.2
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2016-11-02

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

### Install some basic libs and JDK8 (first remove some wrong cache files)
RUN rm -rf /var/lib/apt /etc/apt/sources.list.d/mint.list && apt-get update && \
    apt-get install -y --no-install-recommends software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y zip oracle-java8-installer oracle-java8-set-default && \
    rm -rf /var/lib/apt
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
### Install JAVA JCE policy (necessary for gearman encryption)
RUN apt-get update \
    && apt-get install -y oracle-java8-unlimited-jce-policy \
    && rm -rf /var/lib/apt

### Install Maven
ARG MAVEN_VERSION=3.3.9
ENV MAVEN_HOME /root/apps/maven
RUN mkdir -p $MAVEN_HOME \
  && wget http://www-eu.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz -O- \
    | tar -xzC $MAVEN_HOME --strip-components=1 \
  && ln -s $MAVEN_HOME/bin/mvn /usr/bin/mvn

#### Install some native libaries for Sakuli
RUN apt-get update \
    && apt-get -y install --fix-missing libcv2.4 libtesseract3 wmctrl xdotool psmisc \
    && rm -rf /var/lib/apt/

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
