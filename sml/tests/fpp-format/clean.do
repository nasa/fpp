#!/bin/sh -e

# ----------------------------------------------------------------------
# clean.do
# ----------------------------------------------------------------------

files=`find . -maxdepth 1 -name '*~'`
for file in $files
do
  rm $file
done
cat subdirs.txt | sed 's;$;/clean;' | xargs redo
