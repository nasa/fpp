#!?bin/sh -e

# ----------------------------------------------------------------------
# check.do
# ----------------------------------------------------------------------

. ./defs.sh

mkdir -p check
for file in $FILES
do
  base=`basename $file .adoc`
  echo check/$base.ok check/$base.err
done | xargs redo-ifchange

