#!/bin/sh -e

# ----------------------------------------------------------------------
# clean.do
# ----------------------------------------------------------------------

targets=`find . -mindepth 2 -name clean.do | sed 's/\.do$//'`
redo $targets
rm -f Detailed-Description.adoc *~ *.bak
