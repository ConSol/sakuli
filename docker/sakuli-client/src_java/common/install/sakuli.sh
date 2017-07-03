#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

EXAMPLE_URL=https://github.com/ConSol/sakuli-examples
echo "Install Sakuli Java Example:  Maven  $EXAMPLE_URL to $SAKULI_TEST_SUITE"

tmpdir=/tmp/sakuli-example
mkdir -p $tmpdir
cd $tmpdir
#TODO TS change to master
# https://github.com/ConSol/sakuli-examples/archive/feature/sakuli-se.zip
wget $EXAMPLE_URL/archive/feature/sakuli-se.zip
unzip *.zip
mkdir -p "$SAKULI_TEST_SUITE"
for javasuite in "java-selenium-example" "java-example";  do
    echo "Download Sakuli dependencies for v$SAKULI_VERSION"
    cd $tmpdir/sakuli-examples-feature-sakuli-se/$javasuite
    ls -la
    ## clean target after build, so only dependencies are here
    mvn test clean -Duser.home=$HOME -Dtest=TriggerDownload -DfailIfNoTests=false -Dsakuli.version=$SAKULI_VERSION
done
rm -rf "$SAKULI_TEST_SUITE"
mv $tmpdir/sakuli-examples-feature-sakuli-se/java-example "$SAKULI_TEST_SUITE"
ls -la "$SAKULI_TEST_SUITE"
rm -rvf $tmpdir

echo "add  -Dsakuli.version=$SAKULI_VERSION to Maven startup"
echo "export MAVEN_OPTS=-Dsakuli.version=$SAKULI_VERSION" >> $HOME/.bashrc
echo 'echo MAVEN_OPTS=$MAVEN_OPTS' >> $HOME/.bashrc
