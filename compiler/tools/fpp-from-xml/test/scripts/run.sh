#!/bin/sh

export LOCAL_PATH_PREFIX=`cd $COMPILER_ROOT; echo $PWD`

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_from_xml=$COMPILER_ROOT/bin/fpp-from-xml

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
  $fpp_from_xml $args $infile.xml 2>&1 | remove_path_prefix > $outfile.out.txt
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

if test -f run.sh
then
  . ./run.sh
fi

run_suite $tests
