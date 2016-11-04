#!/bin/sh
./clean.sh
for file in `ls | egrep -e ".md"`; do keydown slides $file; done
mkdir target
cp -fv *.html ./target
cp -rfv ./js ./target
cp -rfv ./css ./target
cp -rfv ./deck.js ./target
cp -rfv ./images ./target

