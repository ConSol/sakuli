#!/usr/bin/env bash
# usage: `update_sakuli_version --old-version v0.x-SNAPSHOT --new-version v0.x`
# the script will update and commit the new version

exit_error() {
    echo "please parameterize the script like e.g. 'update_sakuli_version.sh --new-version 1.0.0'!"
    exit 1
}
new_version=""

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
        *)
            let "i=$i+1"
            ;;
	esac
done

echo "update Dockerfiles to new_version: $new_version"
if [[ ! $new_version ]]; then
	exit_error
fi

searchdir="$(dirname $0)"
find $searchdir -type f -name Dockerfile | while read file ; do \
    sed -i -e "s/^ENV SAKULI_VERSION.*/ENV SAKULI_VERSION $new_version/" $file && \
    echo -e "replace SAKULI_VERSION with '$new_version' in file $file" ; \
    git add $file
    done

git commit -m "update ENV SAKULI_VERSION to '$new_version' in Docker-Containers"