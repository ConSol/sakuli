#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Java JDK 8"

apt-get update
apt-get install -y openjdk-8-jdk
apt-get clean -y