#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Java JRE 8"

apt-get update \
    && apt-get install -y openjdk-8-jre icedtea-plugin \
    && apt-get clean -y