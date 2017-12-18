#!/usr/bin/env bash
set -e -o pipefail

SRC_TAG=$1
TARGET_TAG=$2
SAVEMODE=$3
echo "tag $SRC_TAG -> $TARGET_TAG"
if [[ $SRC_TAG == "" ]] || [[ $TARGET_TAG == "" ]] ; then
  echo "ERROR: execute script like: tag_image.sh <src-tag> <target-tag> [--save]"
  exit -1
fi

IMAGES=(
    "consol/sakuli-ubuntu-xfce"
    "consol/sakuli-ubuntu-xfce-java"
    "consol/sakuli-ubuntu-icewm"
    "consol/sakuli-ubuntu-icewm-java"
    "consol/sakuli-centos-xfce"
    "consol/sakuli-centos-xfce-java"
    "consol/sakuli-centos-icewm"
    "consol/sakuli-centos-icewm-java"
)

#Loop
for IMAGE in "${IMAGES[@]}"
do
	echo "IMAGE: $IMAGE:$SRC_TAG"
	docker pull $IMAGE:$SRC_TAG
	docker run -it -e TESTSUITE_BROWSER=firefox $IMAGE:$SRC_TAG
	docker run -it -e TESTSUITE_BROWSER=chrome $IMAGE:$SRC_TAG
	docker tag $IMAGE:$SRC_TAG $IMAGE:$TARGET_TAG
	if [[ "$SAVEMODE" != "--save" ]] ; then
	    docker push $IMAGE:$TARGET_TAG
    fi
    echo " - done!"
done
