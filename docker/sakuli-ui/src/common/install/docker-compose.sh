#!/usr/bin/env bash
set -e

version=1.19.0
echo "install latest docker-compose $version"
curl -L https://github.com/docker/compose/releases/download/$version/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

echo "------------ docker-compose version -----------"
docker-compose version

echo ".... done!"
