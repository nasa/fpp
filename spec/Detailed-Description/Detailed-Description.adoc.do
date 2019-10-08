#!/bin/sh -e

# ----------------------------------------------------------------------
# Detailed-Description.adoc.do
# ----------------------------------------------------------------------

. ./defs.sh

redo-ifchange $FILES

for file in $FILES
do
  echo
  cat $file
done
