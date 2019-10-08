#!/bin/sh -e

# ----------------------------------------------------------------------
# clean.do
# ----------------------------------------------------------------------

rm -f fpp-spec.adoc *.html *~ *.bak
targets=`find . -mindepth 2 -name clean.do | sed 's/\.do$//'`
redo $targets
