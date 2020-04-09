#!/bin/sh -e

# ----------------------------------------------------------------------
# fpp-spec.do
# ----------------------------------------------------------------------

rm -rf fpp-spec
mkdir $3
cp index.html $3/fpp-spec.html
cp -R code-prettify $3

for file in `find $3 -name '*~'`
do
 rm $file
done
