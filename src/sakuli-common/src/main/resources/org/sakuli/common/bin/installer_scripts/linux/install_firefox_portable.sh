#!/bin/bash
set -e

if [ ! "${1:0:1}" == "" ]; then
    FF_VERS=$1
    if [ ! "${2:0:1}" == "" ]; then
        FF_INST=$2
        echo "download Firefox $FF_VERS and install it to '$FF_INST'."
        mkdir -p "$FF_INST"
        FF_URL=http://releases.mozilla.org/pub/firefox/releases/$FF_VERS/linux-x86_64/en-US/firefox-$FF_VERS.tar.bz2
        wget -qO- $FF_URL | tar xvj -C $FF_INST/
        exit $?
    fi
fi
echo "Script parameter are not set correctly please call it like 'install_firefox_portable.sh [version] [install path]'"
exit -1

