#!/bin/sh -e

# ----------------------------------------------------------------------
# Definitions.adoc.do
# ----------------------------------------------------------------------

. ./defs.sh

redo-ifchange $FILES

for file in $FILES
do
  echo
  cat $file
done
