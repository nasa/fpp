#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_layout=$COMPILER_ROOT/bin/fpp-to-layout

compare()
{
  topDir=$1
  outfile=$2
  diff -u $topDir'Layout.ref/'$outfile.txt $topDir'Layout/'$outfile.txt > $outfile.diff.txt 2>&1
}

compare_out()
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
  $fpp_to_layout $args $infile.fpp 2>&1 | remove_author | remove_path_prefix > $outfile.out.txt
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


. ./run.sh
run_suite $tests
