#!/bin/sh -e

for file in `find . -name '*.asciidoc'`
do
  ispell $file 1>&2
done
