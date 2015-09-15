# This Dockerfile is used to build an sakuli image based on ubuntu

FROM ubuntu:14.04

MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2015-09-14
ENV DEBIAN_FRONTEND noninteractive

ENV JAVA_VERSION 8u60
ENV JAVA_HOME /usr/lib/jvm/java-$JAVA_VERSION

ENV DISPLAY :1
ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 0.9.1-SNAPSHOT
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_ubuntu

########## SSH - SERVER
#RUN apt-get install -y openssh-server
#RUN mkdir /var/run/sshd
#RUN echo 'root:sakuli' | chpasswd
#RUN sed -i 's/PermitRootLogin without-password/PermitRootLogin yes/' /etc/ssh/sshd_config
#
## SSH login fix. Otherwise user is kicked off after login
#RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
#
#ENV NOTVISIBLE "in users profile"
#RUN echo "export VISIBLE=now" >> /etc/profile
#RUN echo "UseDNS no" >> /etc/ssh/sshd_config
#EXPOSE 22

############### add linux-mint dependicies and update packages
RUN apt-key adv --recv-key --keyserver keyserver.ubuntu.com "3EE67F3D0FF405B2"
RUN echo "deb http://packages.linuxmint.com/ rafaela main upstream import" >> /etc/apt/sources.list.d/mint.list \
    && echo "deb http://extra.linuxmint.com/ rafaela main " >> /etc/apt/sources.list.d/mint.list

############### xvnc / xfce installation
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y supervisor vim xfce4 vnc4server wget unzip firefox

##### Add Oracle JAVA JRE8
#RUN apt-get -y install software-properties-common && add-apt-repository ppa:webupd8team/java -y
RUN mkdir -p $JAVA_HOME \
    # download and extract
    && wget -qO- $SAKULI_DOWNLOAD_URL/3rd-party/jre-$JAVA_VERSION-linux-x64.tar.gz | tar xz --strip 1 -C $JAVA_HOME \
    # set alternatives
    && update-alternatives --install "/usr/bin/java" "java" "$JAVA_HOME/bin/java" 1 \
    && update-alternatives --install "/usr/bin/javaws" "javaws" "$JAVA_HOME/bin/javaws" 1 \
    && update-alternatives --install "/usr/lib/firefox/browser/plugins/mozilla-javaplugin.so" "mozilla-javaplugin.so" "$JAVA_HOME/lib/amd64/libnpjp2.so" 1

### Install chrome browser
RUN wget -qO- https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo 'deb http://dl.google.com/linux/chrome/deb/ stable main' >> /etc/apt/sources.list.d/google.list \
    && apt-get update && apt-get -y install google-chrome-stable


### Install Sakuli
##Install some native libaries
RUN apt-get -y install libcv2.4 libtesseract3 wmctrl xdotool psmisc
## Install sakuli zip
RUN wget --directory-prefix=/root $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION.zip \
    && unzip /root/sakuli-v$SAKULI_VERSION.zip -d /root \
    && rm -fv /root/sakuli*.zip \
    && chmod +x -v $(find /root/sakuli '*.sh') \
    && echo "export PATH='$SAKULI_HOME/bin:$PATH'" >> /root/.bashrc


### Install Sahi
RUN wget --directory-prefix=/root \
      $SAKULI_DOWNLOAD_URL/3rd-party/install_sahi_v50_20141105.jar $SAKULI_DOWNLOAD_URL/3rd-party/install_sahi.config \
    #replace installpath with `root/sakuli/sahi`
    && sed -e 's/\(<installpath>\)\([^>|^<]*\)\(<[^>]*\)/\1\/root\/sakuli\/sahi\3/' /root/install_sahi.config \
        > /root/install_sahi_local.config \
    #install sahi
    && java -jar /root/install_sahi_v50_20141105.jar /root/install_sahi_local.config \
    && rm -fv /root/install_sahi*

############### sakuli user
# Add user sakuli to the image
#RUN adduser --quiet sakuli
# Set password for the jenkins user (you may want to alter this).
#RUN echo "sakuli:sakuli" | chpasswd

### Install apps for testing
RUN apt-get -y install gedit gnome-calculator

### CleanUp
RUN apt-get autoclean -y && apt-get autoremove -y

# xvnc server porst, if $DISPLAY=:1 port will be 5901
EXPOSE 5901-5909

ADD .vnc /root/.vnc
ADD .config /root/.config
ADD scripts /root/scripts
RUN chmod +x /root/.vnc/xstartup /etc/X11/xinit/xinitrc /root/scripts/*.sh

CMD ["/root/scripts/startup.sh"]
