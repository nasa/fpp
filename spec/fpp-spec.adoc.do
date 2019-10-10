#!/bin/sh -e

# ----------------------------------------------------------------------
# fpp-spec.adoc.do
# ----------------------------------------------------------------------

. ./defs.sh

redo-ifchange $FILES

echo '= The F Prime Prime (FPP) Modeling Language
:toc: left
:toclevels: 3
:stem:
:source-highlighter: prettify'
for file in $FILES
do
  echo
  cat $file
done | awk -f tags.awk
