# This Dockerfile is used to build an sakuli image based on ubuntu
FROM ubuntu:14.04

MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2015-08-01

ENV HOME /root
ENV DEBIAN_FRONTEND noninteractive
ENV DISPLAY :1

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
#    && apt-get update && apt-get -y dist-upgrade && apt-get upgrade -y


############### sakuli user
# Add user sakuli to the image
RUN adduser --quiet sakuli
# Set password for the jenkins user (you may want to alter this).
RUN echo "sakuli:sakuli" | chpasswd

############### xvnc / xfce installation
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y supervisor xvfb
RUN apt-get install -y xfce4
RUN apt-get install -y vnc4server
RUN apt-get install -y expect

#RUN apt-mark hold initscripts udev plymouth mountall
#RUN apt-get install -y mint-meta-xfce

RUN apt-get autoclean -y && apt-get autoremove -y


ENV VNC_COL_DEPTH 24
ENV VNC_RESOLUTION 1280x1024
ENV VNC_PW sakuli

# xvnc server porst, if $DISPLAY=:1 port will be 5901
EXPOSE 5901-5909

ADD .vnc /root/.vnc
ADD .config /root/.config
ADD scripts /root/scripts
RUN chmod +x /root/.vnc/xstartup /etc/X11/xinit/xinitrc /root/scripts/*.sh

CMD ["/root/scripts/startup.sh"]
