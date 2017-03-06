#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

cd $(dirname `which $0`)
scriptdir=$(pwd)
echo "DIR: $scriptdir"

## check if installed Java JRE is supported by JCE
java -cp $scriptdir/classes KeyLengthDetector

### JCE Download page: http://www.oracle.com/technetwork/java/javase/downloads/index.html
#mkdir -p /tmp/java-jce \
#    && cd /tmp/java-jce \
#    && wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip \
#    && unzip *.zip \
#    && for i in $(find /usr/lib/jvm -name "security"); do /bin/cp -v -f UnlimitedJCEPolicyJDK8/*.jar $i; done \
#    && rm -rf /tmp/java-jce