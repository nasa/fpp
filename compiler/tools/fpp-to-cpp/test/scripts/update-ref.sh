#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_cpp=$COMPILER_ROOT/bin/fpp-to-cpp

update()
{
  args=$1
  infile=$2
  if test -n "$3"
  then
    outfile=$3
  else
    outfile=$infile
  fi
  $fpp_to_cpp $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.ref.txt
}

for file in `find . -name '*.ref.txt'`
do
  rm $file
done

. ./tests.sh

# Default update ref
for t in $tests
do
  echo "
$t()
{
  update '' $t
}"
done > default-update-ref.sh
. ./default-update-ref.sh

move_cpp()
{
  file=$1
  for suffix in hpp cpp
  do
    cp $file'Ac.'$suffix $file'Ac.ref.'$suffix
  done
}

move_template()
{
  file=$1
  for suffix in hpp cpp
  do
    cp $file'.template.'$suffix $file'.template.ref.'$suffix
  done
}

move_test()
{
  file=$1
  for suffix in hpp cpp
  do
    cp $file'TesterBase.'$suffix $file'TesterBase.ref.'$suffix
    cp $file'GTestBase.'$suffix $file'GTestBase.ref.'$suffix
  done
  if test -f $file'TesterHelpers.cpp'
  then
    cp $file'TesterHelpers.cpp' $file'TesterHelpers.ref.cpp'
  fi
}

move_test_template()
{
  file=$1
  for suffix in hpp cpp
  do
    cp $file'Tester.'$suffix $file'Tester.ref.'$suffix
  done
  cp $file'TesterHelpers.cpp' $file'TesterHelpers.ref.cpp'
  cp $file'TestMain.cpp' $file'TestMain.ref.cpp'
}

. ./update-ref.sh

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
