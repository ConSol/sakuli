#!/usr/bin/env bash
# usage: `update_sakuli_version --old-version v0.x-SNAPSHOT --new-version v0.x`
# the script will update the new version

set -e

new_version=""
version_only=""

arguments=($@)
let lastindex=${#arguments[@]}
i=0
while [ $i -le $lastindex ]; do
#    echo "i $i, last $lastindex"
	case ${arguments[$i]} in
        "--new-version")
			let "j=$i+1"
			new_version=${arguments[$j]}
			let "i=$i+2"
			;;
        "--version-only")
			let "j=$i+1"
			version_only="true"
			let "i=$i+2"
			;;
        *)
            let "i=$i+1"
            ;;
	esac
done

echo "=== START: update Sakuli version to new_version: $new_version ==="

basedir=$(dirname $(realpath $0)/)/..
echo "script basedir: $basedir"

exit_error() {
    echo "please parameterize the script like e.g. 'update_sakuli_version.sh --new-version 1.0.0 [--version-only]'!"
    exit 1
}

updateDockerfiles() {
    echo "update Dockerfiles to new_version: $new_version"

    searchdir=$basedir/docker
    refreshDate=$(date +%Y-%m-%d)

    find $searchdir -type f -name Dockerfile* | while read file ; do
        sed -i -e "s/^ARG SAKULI_VERSION.*/ARG SAKULI_VERSION=$new_version/" $file
        echo -e "replace SAKULI_VERSION with '$new_version' in file $file"
        if [[ ! $version_only ]]; then
            sed -i -e "s/^ENV REFRESHED_AT.*/ENV REFRESHED_AT $refreshDate/" $file
            echo -e "replace ENV REFRESHED_AT with '$refreshDate' in file $file"
        fi
        done
}

updateREADME() {
    echo "update README to new_version: $new_version"

    file=$basedir/README.adoc
    sed -i -e "s/^:sakuli-latest-version:.*/:sakuli-latest-version: v$new_version/" $file \
        && echo -e "replace :sakuli-latest-version: with '$new_version' in file $file"
}

if [[ ! $new_version ]]; then
	exit_error
fi

updateDockerfiles
updateREADME

echo "=== FINISHED: update Sakuli version to new_version: $new_version ==="
