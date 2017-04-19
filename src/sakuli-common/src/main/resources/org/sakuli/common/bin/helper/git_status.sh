#!/bin/sh

cd $1

git remote update

LOCAL=$(git rev-parse @)
REMOTE=$(git rev-parse @{u})
BASE=$(git merge-base @ @{u})

if [ $LOCAL = $REMOTE ]; then
    if [[ `git status --porcelain` ]]; then
    echo "Untracked files"
    exit 1
    else
        echo "Up-to-date"
        exit 0
    fi
elif [ $LOCAL = $BASE ]; then
    echo "Need to pull"
    exit 2
elif [ $REMOTE = $BASE ]; then
    echo "Need to push"
    exit 3
else
    echo "Diverged"
    exit 4
fi
