#!/bin/sh -e

# ----------------------------------------------------------------------
# fpp-spec.do
# ----------------------------------------------------------------------

code_prettify=../code-prettify
html=../spec/fpp-spec.html
redo-ifchange $html $code_prettify/COPYING $code_prettify/run_prettify.js
rm -rf fpp-spec
mkdir $3
cp $html $3
cp -R $code_prettify $3

for file in `find $3 -name '*~'`
do
 rm $file
done
