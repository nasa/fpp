#!/bin/sh -e

# ----------------------------------------------------------------------
# fpp-spec.do
# ----------------------------------------------------------------------

redo-ifchange index.html code-prettify/COPYING code-prettify/run_prettify.js
rm -rf fpp-spec
mkdir $3
cp index.html $3/fpp-spec.html
cp -R code-prettify $3

for file in `find $3 -name '*~'`
do
 rm $file
done
