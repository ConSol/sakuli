#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Screenshot-Tool"
labs_url=https://labs.consol.de/sakuli/install/3rd-party/rpm
# scrot checked by: https://www.virustotal.com/de/file/3e7f428363ffbae3618bba35d4ea57ca924a7dd91acc9a8f45e7b239890b2b7c/analysis/1504191366/, source: http://packages.psychotic.ninja/7/base/x86_64/RPMS/scrot-0.8-12.el7.psychotic.x86_64.rpm
# giblib checked by: https://www.virustotal.com/de/file/610b91d1ffc2aa0a41c5668a525b1273bc593f7572471f4bd26837660bb9abd3/analysis/1504275570/, source: ftp://ftp.pbone.net/mirror/ftp5.gwdg.de/pub/opensuse/repositories/home:/Kenzy:/modified:/C7/CentOS_7/x86_64/giblib-1.2.4-2.1.x86_64.rpm
yum -y install $labs_url/scrot-0.8-12.el7.psychotic.x86_64.rpm $labs_url/giblib-1.2.4-2.1.x86_64.rpm
yum clean all