#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Screenshot-Tool"
yum -y install epel-release
rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro
rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-1.el7.nux.noarch.rpm
yum -y --enablerepo=nux-dextop install shutter
yum clean all