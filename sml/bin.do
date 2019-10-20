#!/bin/sh -e

# ----------------------------------------------------------------------
# bin.do
# ----------------------------------------------------------------------

redo-always
rm -rf bin
mkdir $3
tools=`find tools -mindepth 1 -maxdepth 1 -type d | sed 's;^tools/;;'| grep -v '^\.'`
for tool in $tools
do
  redo-ifchange tools/$tool/$tool
  cp tools/$tool/$tool $3/$tool
done
