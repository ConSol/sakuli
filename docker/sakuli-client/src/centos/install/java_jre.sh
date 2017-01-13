#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Java JRE 8"

yum -y install java-1.8.0-openjdk icedtea-web \
    && yum clean all