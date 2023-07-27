#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_cpp=$COMPILER_ROOT/bin/fpp-to-cpp

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
  $fpp_to_cpp $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.out.txt
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

diff_cpp()
{
  file=$1
  target_suffix=$2
  cp $file'Ac'.hpp $file'Ac'$target_suffix.out.hpp && \
  diff -u $file'Ac'$target_suffix.out.hpp $file'Ac'$target_suffix.ref.hpp && \
  cp $file'Ac'.cpp $file'Ac'$target_suffix.out.cpp && \
  diff -u $file'Ac'$target_suffix.out.cpp $file'Ac'$target_suffix.ref.cpp
}

diff_template()
{
  file=$1
  target_suffix=$2
  cp $file.template.hpp $file$target_suffix.out.template.hpp && \
  diff -u $file$target_suffix.out.template.hpp $file$target_suffix.template.ref.hpp && \
  cp $file.template.cpp $file$target_suffix.out.template.cpp && \
  diff -u $file$target_suffix.out.template.cpp $file$target_suffix.template.ref.cpp
}

diff_test()
{
  file=$1
  target_suffix=$2
  cp $file'TesterBase'.hpp $file'TesterBase'$target_suffix.out.hpp && \
  diff -u $file'TesterBase'$target_suffix.out.hpp $file'TesterBase'$target_suffix.ref.hpp && \
  cp $file'TesterBase'.cpp $file'TesterBase'$target_suffix.out.cpp && \
  diff -u $file'TesterBase'$target_suffix.out.cpp $file'TesterBase'$target_suffix.ref.cpp && \
  cp $file'GTestBase'.hpp $file'GTestBase'$target_suffix.out.hpp && \
  diff -u $file'GTestBase'$target_suffix.out.hpp $file'GTestBase'$target_suffix.ref.hpp && \
  cp $file'GTestBase'.cpp $file'GTestBase'$target_suffix.out.cpp && \
  diff -u $file'GTestBase'$target_suffix.out.cpp $file'GTestBase'$target_suffix.ref.cpp
}

diff_test_template()
{
  file=$1
  target_suffix=$2
  cp $file'Tester'.hpp $file'Tester'$target_suffix.out.hpp && \
  diff -u $file'Tester'$target_suffix.out.hpp $file'Tester'$target_suffix.ref.hpp && \
  cp $file'Tester'.cpp $file'Tester'$target_suffix.out.cpp && \
  diff -u $file'Tester'$target_suffix.out.cpp $file'Tester'$target_suffix.ref.cpp && \
  cp $file'TesterHelpers'.cpp $file'TesterHelpers'$target_suffix.out.cpp && \
  diff -u $file'TesterHelpers'$target_suffix.out.cpp $file'TesterHelpers'$target_suffix.ref.cpp && \
  cp $file'TestMain'.cpp $file'TestMain'$target_suffix.out.cpp && \
  diff -u $file'TestMain'$target_suffix.out.cpp $file'TestMain'$target_suffix.ref.cpp
}

. ./run.sh

run_suite $tests
