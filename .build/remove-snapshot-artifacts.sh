#!/usr/bin/env bash
echo ".... remove git tags"
git fetch --tags
tags=$(git tag | grep SNAPSHOT)
echo $tags | xargs -n1 echo
echo "remove remote git tags?"
read -p "Continue (y/n)?" CONT
if [ "$CONT" = "y" ]; then
   echo $tags | xargs -n 1 git tag -d
   echo $tags | xargs -n 1 git push --delete origin
else
  exit 0
fi
