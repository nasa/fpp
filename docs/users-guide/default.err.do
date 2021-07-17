#!/bin/sh -e

# ----------------------------------------------------------------------
# default.err.do
# ----------------------------------------------------------------------

. ./defs.sh

dir=`dirname $2`
base=`basename $2`
infile=$base.adoc
built_in_fpp=../../built-in.fpp
redo-ifchange $infile scripts/extract.awk
mkdir $3

awk -f scripts/extract.awk -v mode=err -v path_prefix="$3/$base"_err_ $infile

dir=$PWD
cd $3
for file in `ls | grep '\.fpp$' || true`
do
  echo 'checking '$file 1>&2
  outfile=$file.out
  if fpp-check $built_in_fpp $file > $outfile 2>&1
  then
    echo "check.do: checking of $file succeeded" 1>&2
    exit 1
  fi
done
cd $dir
