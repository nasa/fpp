#!/bin/sh -e

# ----------------------------------------------------------------------
# clean.do
# ----------------------------------------------------------------------

files=`find . -maxdepth 1 -name '*.fpp' -or -name '*.fpp.out' -or -name '*~'`
for file in $files
do
  rm $file
done
