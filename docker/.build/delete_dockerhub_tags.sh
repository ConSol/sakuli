#!/usr/bin/env bash

set -e -o pipefail

IMAGES=(
    "consol/sakuli-ubuntu-xfce"
    "consol/sakuli-ubuntu-xfce-java"
    "consol/sakuli-ubuntu-icewm"
    "consol/sakuli-ubuntu-icewm-java"
    "consol/sakuli-centos-xfce"
    "consol/sakuli-centos-xfce-java"
    "consol/sakuli-centos-icewm"
    "consol/sakuli-centos-icewm-java"
    "consol/sakuli-ui"
    "consol/omd-labs-ubuntu-sakuli"
    "consol/omd-labs-centos-sakuli"
    "consol/omd-labs-debian-sakuli"
)
PATTERN="-SNAPSHOT"
#PATTERN="v1.1.0-SNAPSHOT"

logfile="docker.delete.images.log"
SAVEMODE=$1

echo "" > $logfile
token=""
function loginToDocker() {
    if [[ $HUB_USERNAME == "" ]] || [[ $HUB_PASSWORD == "" ]] ; then
        echo "please set dockerhub login credentials via env vars: 'HUB_USERNAME' and 'HUB_PASSWORD'"
        exit -1
    fi
    json=$(curl -s --fail -i -X POST \
        -H "Content-Type: application/json" \
        -H "Accept: application/json" \
        -d '{"username":"'${HUB_USERNAME}'","password":"'${HUB_PASSWORD}'"}' \
        https://hub.docker.com/v2/users/login/ | grep "{" )
#    echo $json
#    echo "--------------------"
    token=$(echo $json | jq --raw-output '.token' )

    echo TOKEN: $token
    echo "logged in user: $HUB_USERNAME"
    echo "--------------------"
}

function deleteTags () {
    if [[ $1 == "" ]] ; then
        echo "use function: deleteTag <user/repo>"
        exit -1
    fi

    echo "getTags: ${1}"
    json=$(curl --fail -i -X GET \
      https://hub.docker.com/v2/repositories/${1}/tags/ | grep "{")
#    echo "$json"
#    echo "-------------------------"

    ### ensure continue if no tags are matching
    set +e +o pipefail
    tags=$( echo "$json" | jq --raw-output '.results[].name' | grep $PATTERN )
    set -e -o pipefail

    echo "-------------------------"
    echo "TAGS:"
    echo "$tags"
    echo "-------------------------"

    if [[ $tags == "" ]] ; then echo "no tags" && return ; fi

    for tag in $tags ; do
        echo "delete ${1}:${tag}"
        echo "${1}:${tag}" >> $logfile

        if [[ "$SAVEMODE" != "--save" ]] ; then
            curl --fail -i -X DELETE \
              -H "Accept: application/json" \
              -H "Authorization: JWT $token" \
              "https://hub.docker.com/v2/repositories/${1}/tags/${tag}/"
        fi
        echo "........................."
    done
}

function logout() {
    echo "logout user: $HUB_USERNAME"
    curl -s -i --fail -X POST \
      -H "Accept: application/json" \
      -H "Authorization: JWT $token" \
      https://hub.docker.com/v2/logout/
}

loginToDocker
#deleteTags "consol/sakuli-ubuntu-xfce"
#Loop
for IMAGE in "${IMAGES[@]}" ; do
    deleteTags $IMAGE
done
logout
echo "-----------> DONE!"
