#!/bin/sh

. ../../../scripts/test-utils.sh

fpp_format=../../../bin/fpp-format
fpp_syntax=../../../bin/fpp-syntax
fpp_json=../../../bin/fpp-to-json

compare()
{
  outfile=$1
  diff -u $outfile.ref.txt $outfile.out.txt > $outfile.diff.txt 2>&1
}

run_test()
{
  args=$1
  infile=$2
  if test -n "$3"
  then
    outfile=$3
  else
    outfile=$infile
  fi
  $fpp_json $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.out.txt
  compare $outfile
}

. ./tests.sh

# Default tests
for t in $tests
do
  echo "
$t()
{
  run_test '' $t
}"
done > default-tests.sh
. ./default-tests.sh

