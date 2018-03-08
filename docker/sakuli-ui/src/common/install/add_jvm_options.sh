#!/usr/bin/env bash
### every exit != 0 fails the script
set -e
set -u

echo "Add JVM options"

# add source jvm_options.sh script to set correct java JVM options on startup
echo 'source $STARTUPDIR/jvm_options.sh' >> $HOME/.bashrc