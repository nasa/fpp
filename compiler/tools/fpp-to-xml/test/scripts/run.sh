#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_xml=$COMPILER_ROOT/bin/fpp-to-xml

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
  $fpp_to_xml $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.out.txt
  compare $outfile
}

. ./tests.sh

# Default tests
for t in $tests
do
  echo "
$t()
{
  run_test '-p '$PWD $t
}"
done > default-tests.sh
. ./default-tests.sh

diff_xml()
{
  for file in $@
  do
    if ! diff $file'Ai.xml' $file'Ai.ref.xml'
    then
      return 1
    fi
  done
}

. ./run.sh

run_suite $tests
