#!/usr/bin/env bash

set -e

ROOT="$1"
echo "ROOT DIR: $ROOT"
rm -rfv $ROOT/v*SNAPSHOT
