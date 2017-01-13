#!/usr/bin/env bash
set -e

echo "Install Sakuli v$SAKULI_VERSION to '$SAKULI_ROOT'"

SAKULI_DOWNLOAD_URL=https://labs.consol.de/sakuli/install

### insert env content of $SAKULI_ROOT to sakuli install config (iz-pack currently don't support this)
sed -i -e 's@SAKULI_ROOT@'"$SAKULI_ROOT"'@' $INST_SCRIPTS/sakuli-auto-install.xml
cat $INST_SCRIPTS/sakuli-auto-install.xml

echo "Download Sakuli installer from $SAKULI_DOWNLOAD_URL ..."
wget -nv --directory-prefix=/tmp/sakuli $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION-installer.jar
mkdir -p $SAKULI_HOME
java -jar /tmp/sakuli/sakuli-v$SAKULI_VERSION-installer.jar $INST_SCRIPTS/sakuli-auto-install.xml
rm -rf /tmp/sakuli

echo "Installed Sakuli:"
$SAKULI_HOME/bin/sakuli -version
