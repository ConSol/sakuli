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
HTTP="/"
OUTPUT="$2"
OUTPUT_DIR=$(dirname $OUTPUT)

echo "ROOT DIR: $ROOT"
echo "OUTPUT FILE: $OUTPUT"
echo "OUTPUT DIR: $OUTPUT_DIR"
mkdir -p $OUTPUT_DIR

function createLatest (){
    echo "------ update latest files ----"
    latestDocRoot=$( find "$ROOT" -maxdepth 1 -mindepth 1 -name 'v*' -type d -print0 | xargs -0 ls -dt | grep -iv snapshot | head -1)
    echo "latest Doc: $latestDocRoot"

    targetDocRoot="$ROOT/latest"
    if [ -d "$targetDocRoot" ]; then
        rm -rf $targetDocRoot;
    fi

    echo "copy '$latestDocRoot' -> '$targetDocRoot'"
    cp -r $latestDocRoot $targetDocRoot
    mv -v $(find $targetDocRoot -name 'sakuli_documentation*.pdf') $targetDocRoot/pdf/sakuli_documentation_latest.pdf
    echo "------ finished: update latest files ----"
}

function createHeader (){
    echo ":docinfo1:

= Sakuli Documentations

image::sakuli_logo_smaller.png[sakuli-logo]

Below you will find all currently available documentations for the different sakuli versions:


|===
|Version | HTML | PDF" > $OUTPUT
}

function addFolderEntryToAdoc (){
    pattern="$1"
    echo "add folder with '$pattern' to adoc"

    for filepath in `find "$ROOT" -maxdepth 1 -mindepth 1 -name $pattern -type d| sort`; do
      folderName=`basename "$filepath"`
      echo "generate adoc table entry for '$folderName'"
      echo "-------------------------------------------"
      echo "
|$folderName
|link:$folderName/index.html[HTML]
|link:$folderName/pdf/sakuli_documentation_$folderName.pdf[PDF^]
" >> $OUTPUT
    done
}

function createrFooter (){
    echo "|===" >> $OUTPUT
    echo "Version _latest_ is pointing to the latest *released stable* version (no SNAPSHOT versions) 😉" >> $OUTPUT
}

#### generate css for footer
function copyDesignFile (){
    SCRIPT=`realpath -s $0`
    SCRIPTPATH=`dirname $SCRIPT`
    cp -v $SCRIPTPATH/docinfo*.html $OUTPUT_DIR/
}

createLatest
createHeader
addFolderEntryToAdoc 'latest'
addFolderEntryToAdoc 'v*'
createrFooter

cat $OUTPUT

