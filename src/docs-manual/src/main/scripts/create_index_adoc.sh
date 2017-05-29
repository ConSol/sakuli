#!/usr/bin/env bash

#cd "$1"
#cont="<html><body><ul>"
#
##find . -name 'v*'  -type d #-exec echo {} + >> cont
#
#echo $cont
#perl -e 'print ; while(<>) { chop $_; print "<li><a href=\"./$_\">$_</a></li>";} print "</ul></body></html>"' > index.html

#!/bin/bash

ROOT="$1"
HTTP="/"
OUTPUT="$2"
OUTPUT_DIR=$(dirname $OUTPUT)

echo "ROOT DIR: $ROOT"
echo "OUTPUT FILE: $OUTPUT"
echo "OUTPUT DIR: $OUTPUT_DIR"
mkdir -p $OUTPUT_DIR

echo ":docinfo1:

= Sakuli Documentations

image::sakuli_logo_smaller.png[sakuli-logo]

Bellow you will find all currently available documentations for the different sakuli versions:


|===
|Version | HTML | PDF" > $OUTPUT

i=0
for filepath in `find "$ROOT" -maxdepth 1 -mindepth 1 -name 'v*' -type d| sort`; do
  folderName=`basename "$filepath"`
  echo "generate adoc table entry for '$folderName'"
  echo "-------------------------------------------"
  echo "
|$folderName
|link:$folderName/index.html[HTML]
|link:$folderName/pdf/sakuli_documentation_$folderName.pdf[PDF^]
" >> $OUTPUT
done

echo "|===" >> $OUTPUT

cat $OUTPUT

#### generate css for footer
SCRIPT=`realpath -s $0`
SCRIPTPATH=`dirname $SCRIPT`
cp -v $SCRIPTPATH/docinfo*.html $OUTPUT_DIR/
