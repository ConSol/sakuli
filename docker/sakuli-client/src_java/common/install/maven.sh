#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Maven  $MAVEN_VERSION"
mkdir -p $MAVEN_HOME
wget http://www-eu.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz -O- \
    | tar -xzC $MAVEN_HOME --strip-components=1

echo '$MAVEN_HOME/bin/mvn $MAVEN_OPTS "$@"' > /usr/bin/mvn
chmod +x /usr/bin/mvn