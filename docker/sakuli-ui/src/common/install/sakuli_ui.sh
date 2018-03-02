#!/usr/bin/env bash
### every exit != 0 fails the script
set -e
set -u

LABS_URL=https://labs.consol.de/sakuli/install/sakuli-v${SAKULI_VERSION}.zip
dest=${SAKULI_UI_INST_DIR}
echo "Download and Install Sakuli UI:  $dest"

tmpdir=/tmp/sakuli-ui
mkdir -p $tmpdir && cd $tmpdir

mkdir -p "$dest"

echo "DOWNLOAD URL: $LABS_URL"
wget $LABS_URL
unzip -j *.zip  '*/sakuli-ui-web.jar' -d "$dest/"

echo "set correct permissions"
$INST_SCRIPTS/set_user_permission.sh $dest

rm -rf $tmpdir