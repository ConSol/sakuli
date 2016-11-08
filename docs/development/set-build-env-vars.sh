#!/usr/bin/env bash
#WORKSPACE=$(pwd)
#GIT_BRANCH=origin/feature/#219_sahi_error
#SAKULI_VERSION=v1-SNAPSHOT-219_sahi_error
#SAKULI_VERSION=v1-SNAPSHOT

echo "branch= $GIT_BRANCH"
SAKULI_BRANCH=${GIT_BRANCH/origin\/}
echo "SAKULI_BRANCH=$SAKULI_BRANCH" > $WORKSPACE/myjob.properties

FEATURE_NAME=${GIT_BRANCH:$(expr index \"$GIT_BRANCH\" '#') -1 }
echo "FEATURE_NAME=$FEATURE_NAME" >> $WORKSPACE/myjob.properties

SAKULI_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\[')"
echo "SAKULI_VERSION=$SAKULI_VERSION" >> $WORKSPACE/myjob.properties
VERSION_SURFIX=${SAKULI_VERSION:$(expr index \"$SAKULI_VERSION\" 'SNAPSHOT') +7 }
echo "VERSION_SURFIX=$VERSION_SURFIX" >> $WORKSPACE/myjob.properties

cat $WORKSPACE/myjob.properties
