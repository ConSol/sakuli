#!/usr/bin/env bash
# usage: `update_sakuli_version --old-version v0.x-SNAPSHOT --new-version v0.x`
# the script will update and commit the new version

exit_error() {
    echo "please parameterize the script like e.g. 'update_sakuli_version.sh --old-version v0.9.1-SNAPSHOT --new-version v0.9.1'!"
    exit 1
}
old_version=""
new_version=""

arguments=($@)
let lastindex=${#arguments[@]}
i=0
while [ $i -le $lastindex ]; do
#    echo "i $i, last $lastindex"
	case ${arguments[$i]} in
		"--old-version")
			let "j=$i+1"
			old_version=${arguments[$j]}
			let "i=$i+2"
			;;
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

echo "old_version: $old_version, new_version: $new_version"
if [[ ! $old_version ]]; then
	exit_error
fi
if [[ ! $new_version ]]; then
	exit_error
fi

searchdir="$(dirname $0)"
find $searchdir -type f -name Dockerfile | while read file ; do \
    sed -i -e "s/SAKULI_VERSION $old_version/SAKULI_VERSION $new_version/" $file && \
    echo -e "replace SAKULI_VERSION '$old_version' with '$new_version' in file $file" ; \
    git add $file
    done

git commit -m "update SAKULI_VERSION '$old_version' to '$new_version' in Docker-Containers"