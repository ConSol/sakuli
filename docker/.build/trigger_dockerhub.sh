#!/usr/bin/env bash
set -e -o pipefail
echo "trigger dockerhub builds for Tag $GIT_TAG:"

URLS=(
    "https://registry.hub.docker.com/u/consol/sakuli-ubuntu-xfce/trigger/76b9b474-97e7-472a-a807-fa1829ef4b93/"
    "https://registry.hub.docker.com/u/consol/sakuli-ubuntu-xfce-java/trigger/62342ff8-d1cb-4111-b298-e43ffdf3d4f7/"
	"https://registry.hub.docker.com/u/consol/sakuli-centos-xfce/trigger/5174c3a9-dc8d-4180-b9d5-97e47c119799/"
	"https://registry.hub.docker.com/u/consol/sakuli-centos-xfce-java/trigger/f42ab405-b103-4544-9ed5-84d37dd3ef78/"
)
PAYLOAD='{"source_type": "Tag", "source_name": "'$GIT_TAG'"}'

#Loop
for URL in "${URLS[@]}"
do
	echo "URL: $URL"
  	echo "PAYLOAD: $PAYLOAD"
    set -x
    curl -H "Content-Type: application/json" --data "$PAYLOAD" -X POST "$URL"
    set +x
    echo " - done!"
done