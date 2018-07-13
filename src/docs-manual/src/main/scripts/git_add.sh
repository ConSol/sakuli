#!/usr/bin/env bash
set -e

ROOT="$1"
echo "ROOT DIR: $ROOT"
cd $ROOT && git add -Av