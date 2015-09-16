# This Dockerfile is used to build an sakuli image based on Centos

FROM centos:centos7
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2015-09-15

ENV JAVA_VERSION 8u60

ENV DISPLAY :1
ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

ENV SAKULI_DOWNLOAD_URL https://labs.consol.de/sakuli/install
ENV SAKULI_VERSION 0.9.1-SNAPSHOT
ENV SAKULI_HOME /root/sakuli/sakuli-v$SAKULI_VERSION
ENV SAKULI_TEST_SUITE /root/sakuli/example_test_suites/example_ubuntu

# Install the appropriate software
RUN yum -y update; yum clean all
RUN yum -y install epel-release; yum clean all
RUN yum -y install sudo
RUN yum --enablerepo=epel -y -x gnome-keyring --skip-broken groups install "Xfce"
RUN yum -y groups install "Fonts"
RUN yum -y install tigervnc-server wget unzip firefox

### Install java and java-plugin
RUN yum -y install $SAKULI_DOWNLOAD_URL/3rd-party/jre-$JAVA_VERSION-linux-x64.rpm
# creat symbolic link for firefox java plugin
RUN ln -s /usr/java/default/lib/amd64/libnpjp2.so /usr/lib64/mozilla/plugins/

### Install chrome browser
ADD repos /etc/yum.repos.d/
RUN yum -y install google-chrome-stable \
    && echo "alias google-chrome='/usr/bin/google-chrome --user-data-dir'" >> /root/.bashrc \
    && echo "alias google-chrome='/usr/bin/google-chrome -no-default-browser-check --user-data-dir'" >> /etc/bashrc

#--no-default-browser-check
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
## Install sakuli zip
RUN wget --directory-prefix=/root $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION.zip \
    && unzip /root/sakuli-v$SAKULI_VERSION.zip -d /root \
    && rm -fv /root/sakuli*.zip \
    && chmod +x -v $(find /root/sakuli '*.sh')


### Install Sahi
RUN wget --directory-prefix=/root \
      $SAKULI_DOWNLOAD_URL/3rd-party/install_sahi_v50_20141105.jar $SAKULI_DOWNLOAD_URL/3rd-party/install_sahi.config \
    #replace installpath with `root/sakuli/sahi`
    && sed -e 's/\(<installpath>\)\([^>|^<]*\)\(<[^>]*\)/\1\/root\/sakuli\/sahi\3/' /root/install_sahi.config \
        > /root/install_sahi_local.config \
    #install sahi
    && java -jar /root/install_sahi_v50_20141105.jar /root/install_sahi_local.config \
    && rm -fv /root/install_sahi*
#config chrome start 'without asking default browser'
ADD .config/google-chrome /root/sakuli/sahi/userdata/browser/chrome/profiles/sahi0


### Install apps for testing
RUN yum -y install gedit gnome-calculator

### CleanUp
RUN yum clean all

# xvnc server porst, if $DISPLAY=:1 port will be 5901
EXPOSE 5901-5909

ADD .vnc /root/.vnc
ADD .config /root/.config
ADD scripts /root/scripts
RUN chmod +x  /root/scripts/*.sh /root/.vnc/xstartup /etc/xdg/xfce4/xinitrc
RUN /bin/dbus-uuidgen > /etc/machine-id

CMD ["/root/scripts/startup.sh"]
