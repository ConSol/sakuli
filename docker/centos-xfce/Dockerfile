# "ported" by Adam Miller <maxamillion@fedoraproject.org> from
#   https://github.com/fedora-cloud/Fedora-Dockerfiles
#
# Originally written for Fedora-Dockerfiles by
#   scollier <emailscottcollier@gmail.com>

FROM centos:centos7
MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2015-08-01

# Install the appropriate software
RUN yum -y update; yum clean all
RUN yum -y install epel-release; yum clean all
RUN yum -y install sudo
RUN yum --enablerepo=epel -y -x gnome-keyring --skip-broken groups install "Xfce"
RUN yum -y groups install "Fonts"
RUN yum -y install tigervnc-server expect firefox
RUN yum clean all


ENV DISPLAY :1
ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

# xvnc server porst, if $DISPLAY=:1 port will be 5901
EXPOSE 5901-5909

ADD .vnc /root/.vnc
ADD .config /root/.config
ADD scripts /root/scripts
#RUN chmod +x  /etc/X11/xinit/xinitrc /root/scripts/*.sh
RUN chmod +x  /root/scripts/*.sh /root/.vnc/xstartup /etc/xdg/xfce4/xinitrc
RUN /bin/dbus-uuidgen > /etc/machine-id

CMD ["/root/scripts/startup.sh"]
