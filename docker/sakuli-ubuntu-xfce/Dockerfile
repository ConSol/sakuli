# This Dockerfile is used to build an sakuli image based on Ubuntu

FROM consol/ubuntu-xfce-vnc
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2015-09-21

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 0.9.1-SNAPSHOT
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_xfce

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

### Install apps for testing
RUN apt-get -y install gedit gnome-calculator

# xvnc server porst, if $DISPLAY=:1 port will be 5901
EXPOSE 5901

ADD scripts/sakuli_startup.sh /root/scripts/
RUN chmod +x /root/scripts/*.sh
CMD ["/root/scripts/sakuli_startup.sh"]