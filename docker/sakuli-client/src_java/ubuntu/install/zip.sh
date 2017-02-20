#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Zip"

apt-get update
apt-get install -y unzip zip
apt-get clean -y