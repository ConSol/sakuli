#!/usr/bin/env bash
### every exit != 0 fails the script
set -e
set -u

echo "Install Docker CLI from Docker Inc. repositories."
curl -sSL https://get.docker.com/ | sh \
    && useradd $SAKULI_UI_DOCKER_USER_ID -m -s /bin/bash \
    && usermod -aG docker $SAKULI_UI_DOCKER_USER_ID

apt-get clean -y