#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Zip"

yum -y install unzip zip
yum clean all