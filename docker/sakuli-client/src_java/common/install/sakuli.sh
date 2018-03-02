#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

EXAMPLE_URL=https://github.com/ConSol/sakuli-examples
echo "Install Sakuli Java Example:  Maven  $EXAMPLE_URL to $SAKULI_TEST_SUITE"

mkdir -p /tmp/sakuli-example
cd /tmp/sakuli-example
wget $EXAMPLE_URL/archive/master.zip
unzip *.zip
mkdir -p "$SAKULI_TEST_SUITE"
mv sakuli-examples-master/java-example/* "$SAKULI_TEST_SUITE"
rm -rf /tmp/sakuli-example

echo "Download Sakuli dependencies for v$SAKULI_VERSION"
cd $SAKULI_TEST_SUITE
ls -la
## clean target after build, so only dependencies are here
mvn test clean -Duser.home=$HOME -Dtest=TriggerDownload -DfailIfNoTests=false -Dsakuli.version=$SAKULI_VERSION

echo "add  -Dsakuli.version=$SAKULI_VERSION to Maven startup"
echo "export MAVEN_OPTS=-Dsakuli.version=$SAKULI_VERSION" >> $HOME/.bashrc
echo 'echo MAVEN_OPTS=$MAVEN_OPTS' >> $HOME/.bashrc

echo "set correct permissions"
$INST_SCRIPTS/set_user_permission.sh $SAKULI_TEST_SUITE