#!/bin/sh -e

# ----------------------------------------------------------------------
# clean.do
# ----------------------------------------------------------------------

rm -f manual.adoc *.html *~ *.bak
targets=`find . -mindepth 2 -name clean.do | sed 's/\.do$//'`
redo $targets
