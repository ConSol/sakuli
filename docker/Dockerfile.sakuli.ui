# This Dockerfile is used to build a sakuli image based on Ubuntu

FROM openjdk:8-jre

MAINTAINER Tobias Schneck "tobias.schneck@consol.de"
ENV REFRESHED_AT 2018-06-27

#### currently not supported
#LABEL io.k8s.description="Sakuli UI container for managing Sakuli tests" \
#      io.k8s.display-name="Sakuli UI container" \
#      io.openshift.expose-services="8080:http" \
#      io.openshift.tags="sakuli, java, sakuli-ui" \
#      io.openshift.non-scalable=true

### INST config
ENV SAKULI_UI_INST_DIR=/app \
    TERM=xterm \
    STARTUPDIR=/dockerstartup \
    INST_SCRIPTS=/root/install

### Environment config (overwritable)
ENV HTTP_PORT=8080 \
    SAKULI_UI_USER=admin \
    SAKULI_UI_PW=sakuli \
    SAKULI_UI_DOCKER_USER_ID=1000 \
    SAKULI_ROOT_DIR=/opt/sakuli-ui-root

ADD ./sakuli-ui/src/common/install/ $INST_SCRIPTS/
RUN find $INST_SCRIPTS -name '*.sh' -exec chmod a+x {} +

### Intsall basics for jvm & docker
RUN $INST_SCRIPTS/add_jvm_options.sh
RUN $INST_SCRIPTS/docker.sh
RUN $INST_SCRIPTS/docker-compose.sh

### Install Sakuli UI
ARG SAKULI_VERSION=1.2.0
RUN $INST_SCRIPTS/sakuli_ui.sh

#### configure startup
ADD ./sakuli-ui/src/common/scripts/ $STARTUPDIR
RUN $INST_SCRIPTS/set_user_permission.sh $STARTUPDIR $HOME

## Connection port for Web Application:
EXPOSE $HTTP_PORT
USER $SAKULI_UI_DOCKER_USER_ID

ENTRYPOINT ["/dockerstartup/startup.sh"]