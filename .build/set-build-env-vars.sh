#!/usr/bin/env bash
#WORKSPACE=$(pwd)
#GIT_BRANCH=origin/feature/#219_sahi_error
#SAKULI_VERSION=v1-SNAPSHOT-219_sahi_error
#SAKULI_VERSION=v1-SNAPSHOT
set -u

echo "branch= $GIT_BRANCH"
SAKULI_BRANCH=${GIT_BRANCH/origin\/}
echo "SAKULI_BRANCH=$SAKULI_BRANCH" > $WORKSPACE/myjob.properties

SAKULI_VERSION="$(mvn -q \
    -Dexec.executable="echo" \
    -Dexec.args='${project.version}' \
    --non-recursive \
    org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)"

echo "SAKULI_VERSION=$SAKULI_VERSION" >> $WORKSPACE/myjob.properties
#no difference since https://github.com/ConSol/sakuli/issues/317
echo "SAKULI_FEATURE_VERSION=$SAKULI_VERSION" >> $WORKSPACE/myjob.properties

if [[ $SAKULI_BRANCH == dev ]] || [[ $SAKULI_BRANCH == feature* ]] ; then
    SAKULI_DOC_BRANCH=$SAKULI_BRANCH
else
    SAKULI_DOC_BRANCH=master
fi
echo "SAKULI_DOC_BRANCH=$SAKULI_DOC_BRANCH" >> $WORKSPACE/myjob.properties

# jenkins will use the myjob.properties as environment vars
cat $WORKSPACE/myjob.properties
