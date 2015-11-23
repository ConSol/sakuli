# This Dockerfile is used to build an sakuli image based on Ubuntu

FROM consol/ubuntu-xfce-vnc
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2015-11-23

ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 0.9.2-SNAPSHOT
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
    # modify chrome startup options
    && sed -e 's|--incognito|--incognito --no-default-browser-check --no-first-run --disable-infobars|g' -i /root/sakuli/sahi/config/browser_types/linux.xml \
    && rm -fv /root/install_sahi*

### Install apps for testing
RUN apt-get update && apt-get -y install gedit gnome-calculator

# xvnc server port, if $DISPLAY=:1 port will be 5901
EXPOSE 5901
# novnc web port
EXPOSE 6901

ADD scripts/sakuli_startup.sh /root/scripts/
RUN chmod +x /root/scripts/*.sh

# startup script
ENTRYPOINT ["/root/scripts/sakuli_startup.sh"]
# arguments for the sakuli_startup.sh script
CMD ["--run $SAKULI_TEST_SUITE"]
