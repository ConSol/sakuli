#!/usr/bin/env bash
set -e -o pipefail
echo "trigger dockerhub builds for Tag $GIT_TAG:"

URLS=(
    "https://registry.hub.docker.com/u/consol/sakuli-ubuntu-xfce/trigger/76b9b474-97e7-472a-a807-fa1829ef4b93/"
    "https://registry.hub.docker.com/u/consol/omd-labs-ubuntu-sakuli/trigger/6cdc0a55-5926-4fd7-81ce-d97e97aee63c/"
    "https://registry.hub.docker.com/u/consol/sakuli-ubuntu-xfce-java/trigger/62342ff8-d1cb-4111-b298-e43ffdf3d4f7/"
    "https://registry.hub.docker.com/u/consol/sakuli-ubuntu-icewm/trigger/453572ea-3272-46b6-a8fb-9ef95c4e655d/"
    "https://registry.hub.docker.com/u/consol/sakuli-ubuntu-icewm-java/trigger/41998b27-c887-4500-b913-9ebf8f643ab7/"
    "https://registry.hub.docker.com/u/consol/sakuli-centos-xfce/trigger/5174c3a9-dc8d-4180-b9d5-97e47c119799/"
    "https://registry.hub.docker.com/u/consol/omd-labs-centos-sakuli/trigger/1541eb97-7147-4100-b9de-371f74f930cb/"
    "https://registry.hub.docker.com/u/consol/sakuli-centos-xfce-java/trigger/f42ab405-b103-4544-9ed5-84d37dd3ef78/"
    "https://registry.hub.docker.com/u/consol/sakuli-centos-icewm/trigger/8edc4936-c7d8-4618-9c55-b541ba457454/"
    "https://registry.hub.docker.com/u/consol/sakuli-centos-icewm-java/trigger/b5793202-aa0b-47fa-956a-49704f7c6a1e/"
    "https://registry.hub.docker.com/u/consol/omd-labs-debian-sakuli/trigger/f5484b4e-d50d-4c89-be91-edab54ff1446/"
)

### starting with `v`
if [[ ${GIT_TAG:0:1} == "v" ]] ; then
    PAYLOAD='{"source_type": "Tag", "source_name": "'$GIT_TAG'"}'
elif [[ $GIT_TAG == "master" ]] ; then
    PAYLOAD='{"docker_tag": "'latest'"}'
else
    PAYLOAD='{"docker_tag": "'$GIT_TAG'"}'
fi

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