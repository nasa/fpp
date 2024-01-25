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
  $fpp_to_cpp $args $infile.fpp 2>&1 | remove_author | remove_path_prefix > $outfile.out.txt
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
  if test $# -ne 1
  then
    echo 'usage: diff_cpp file' 1>&2
    exit 1
  fi
  file=$1
  diff -u $file'Ac.ref.hpp' $file'Ac.hpp' && \
  diff -u $file'Ac.ref.cpp' $file'Ac.cpp'
}

diff_cpp_suffix()
{
  if test $# -lt 1 || test $# -gt 2
  then
    echo 'usage: diff_cpp_suffix file [suffix]' 1>&2
    exit 1
  fi
  file=$1
  target_suffix=$2
  cp $file'Ac'.hpp $file'Ac'$target_suffix.out.hpp && \
  diff -u $file'Ac'$target_suffix.ref.hpp $file'Ac'$target_suffix.out.hpp && \
  cp $file'Ac'.cpp $file'Ac'$target_suffix.out.cpp && \
  diff -u $file'Ac'$target_suffix.ref.cpp $file'Ac'$target_suffix.out.cpp
}

diff_template()
{
  file=$1
  remove_author < $file.template.hpp > $file.template.out.hpp && \
  diff -u $file.template.ref.hpp $file.template.out.hpp && \
  remove_author < $file.template.cpp > $file.template.out.cpp && \
  diff -u $file.template.ref.cpp $file.template.out.cpp
}

diff_test()
{
  file=$1
  diff -u $file'TesterBase.ref.hpp' $file'TesterBase.hpp' && \
  diff -u $file'TesterBase.ref.cpp' $file'TesterBase.cpp' && \
  diff -u $file'GTestBase.ref.hpp' $file'GTestBase.hpp' && \
  diff -u $file'GTestBase.ref.cpp' $file'GTestBase.cpp' && \
  if test -f $file'TesterHelpers.ref.cpp'
  then
    diff -u $file'TesterHelpers.ref.cpp' $file'TesterHelpers.cpp'
  fi
}

diff_test_template()
{
  file=$1
  remove_author < $file'Tester.hpp' > $file'Tester.out.hpp' && \
  diff -u $file'Tester.ref.hpp' $file'Tester.out.hpp' && \
  remove_author < $file'Tester.cpp' > $file'Tester.out.cpp' && \
  diff -u $file'Tester.ref.cpp' $file'Tester.out.cpp' && \
  if test -f $file'TesterHelpers.ref.cpp'
  then
    diff -u $file'TesterHelpers.ref.cpp' $file'TesterHelpers.cpp'
  fi && \
  remove_author < $file'TestMain.cpp' > $file'TestMain.out.cpp' && \
  diff -u $file'TestMain.ref.cpp' $file'TestMain.out.cpp'
}

. ./run.sh

run_suite $tests
