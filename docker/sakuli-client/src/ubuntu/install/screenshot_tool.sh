#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Screenshot-Tool"
apt-get update 
apt-get install -y scrot
apt-get clean -y