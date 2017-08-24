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
# libxkbcommon (checked by https://www.virustotal.com/de/file/5490412624f96da2d86c37c047394eb62ac261aa69a7883372fe9d5071302ff7/analysis/1503593211/, orig: ftp://195.220.108.108/linux/fedora/linux/releases/24/Everything/x86_64/os/Packages/l/libxkbcommon-0.5.0-4.fc24.x86_64.rpm
yum -y install \
    https://labs.consol.de/sakuli/install/3rd-party/rpm/libxkbcommon-0.5.0-4.fc24.x86_64.rpm \
    xdotool \
    redhat-lsb-core \
    psmisc \
    https://labs.consol.de/sakuli/install/3rd-party/rpm/wmctrl-1.07-17.gf.el7.x86_64.rpm

yum clean all