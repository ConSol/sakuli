#!/usr/bin/env bash
VERSION_SURFIX=${SAKULI_VERSION:$(expr index \"$SAKULI_VERSION\" 'SNAPSHOT') +7 }
echo "VERSION_SURFIX: $VERSION_SURFIX"

if [[ $VERSION_SURFIX == "" ]]; then
    echo "change version!"
    exit 0
fi
exit 1