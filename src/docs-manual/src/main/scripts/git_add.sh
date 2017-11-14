#!/usr/bin/env bash

#cd "$1"
#cont="<html><body><ul>"
#
##find . -name 'v*'  -type d #-exec echo {} + >> cont
#
#echo $cont
#perl -e 'print ; while(<>) { chop $_; print "<li><a href=\"./$_\">$_</a></li>";} print "</ul></body></html>"' > index.html

#!/bin/bash
set -e

ROOT="$1"
echo "ROOT DIR: $ROOT"
cd $ROOT && git add -Av