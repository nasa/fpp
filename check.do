#!/bin/sh -e

# ----------------------------------------------------------------------
# check.do
# ----------------------------------------------------------------------

. ./defs.sh

for file in `egrep -rlL 'NO_CHECK' --include=\*.tnt src`
do
    echo $file >&2
    sed -e '/# Error/d' $file | tnet-check || exit 1
done
