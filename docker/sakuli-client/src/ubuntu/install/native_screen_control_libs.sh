#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install native screen control libraries for Sakuli"
apt-get update 
apt-get install -y --fix-missing libcv2.4 libtesseract3 wmctrl xdotool psmisc lsb-release
apt-get clean -y