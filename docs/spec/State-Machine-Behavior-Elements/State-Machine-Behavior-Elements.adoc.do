#!/bin/sh -e

# ----------------------------------------------------------------------
# State-Machine-Behavior.adoc.do
# ----------------------------------------------------------------------

. ./defs.sh

redo-ifchange $FILES

for file in $FILES
do
  echo
  cat $file
done
