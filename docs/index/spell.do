#!/bin/sh -e

# ----------------------------------------------------------------------
# spell.do
# ----------------------------------------------------------------------

. ./defs.sh

# Check this directory
for file in $FILES
do
  if ! echo $file | grep -q '/'
  then
    ispell $file 1>&2
  fi
done

# Check this subdirectories
targets=`find . -mindepth 2 -name spell.do | sed 's/\.do$//'`
for target in $targets
do
  redo $target
done
