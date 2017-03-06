#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Java JDK 8"

yum -y install java-1.8.0-openjdk-devel
yum clean all