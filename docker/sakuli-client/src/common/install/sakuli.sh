#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

### Install Sakuli in the $SAKULI_VERSION and create the example testsuite
### under $SAKULI_TEST_SUITE

echo "Install Sakuli v$SAKULI_VERSION to '$SAKULI_ROOT' and create the example testsuite under '$SAKULI_TEST_SUITE'"

SAKULI_DOWNLOAD_URL=https://labs.consol.de/sakuli/install

### insert env content of $SAKULI_ROOT to sakuli install config (iz-pack currently don't support this)
sed -i -e 's@SAKULI_ROOT@'"$SAKULI_ROOT"'@' $INST_SCRIPTS/sakuli-auto-install.xml
cat $INST_SCRIPTS/sakuli-auto-install.xml

echo "Download Sakuli installer from $SAKULI_DOWNLOAD_URL ..."
wget -nv --directory-prefix=/tmp/sakuli $SAKULI_DOWNLOAD_URL/sakuli-v$SAKULI_VERSION-installer.jar
mkdir -p $SAKULI_HOME
java -jar /tmp/sakuli/sakuli-v$SAKULI_VERSION-installer.jar $INST_SCRIPTS/sakuli-auto-install.xml
rm -rf /tmp/sakuli

example_suite=$SAKULI_ROOT/example_test_suites/example_xfce
echo "Link example suite '$example_suite' to volume mount point: $SAKULI_TEST_SUITE:"
cp -r $example_suite $SAKULI_TEST_SUITE

echo "add to $HOME/.bashrc: use correct UMASK '${SAKULI_UMASK:-0000}'"
# Important for folder permissions to ensure that volume-mounted log
# files can be deleted afterwards
echo 'umask ${SAKULI_UMASK:-0000} && echo umask set to $(umask)' >> $HOME/.bashrc

echo -e "\n\nInstalled Sakuli:"
$SAKULI_HOME/bin/sakuli -version
