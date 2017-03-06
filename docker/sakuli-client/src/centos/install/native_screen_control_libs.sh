#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install native screen control libraries for Sakuli"

# opencv
yum -y install opencv

# tesseract
yum -y install leptonica tesseract tesseract-langpack-deu

# some missing ui controll tools
# wmctrl  (checked by: https://www.virustotal.com/de/url/394da2ac7e9bbbf5d10a3f304cc913459fc38f66019a4c3ba0fd4afd8f48e64f/analysis/1465466765/)
yum -y install xdotool redhat-lsb-core psmisc https://labs.consol.de/sakuli/install/3rd-party/rpm/wmctrl-1.07-17.gf.el7.x86_64.rpm

yum clean all