#!/bin/sh

. ../../../../scripts/test-utils.sh

fpp_to_cpp=../../../../bin/fpp-to-cpp

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

constants()
{
  run_test "-n constants.names.txt -p $PWD" constants && \
    diff -u constants.names.txt constants.names.ref.txt && \
    remove_year < FppConstantsAc.hpp > FppConstantsAc_constants.out.hpp && \
    diff -u FppConstantsAc_constants.out.hpp FppConstantsAc_constants.ref.hpp && \
    remove_year < FppConstantsAc.cpp > FppConstantsAc_constants.out.cpp && \
    diff -u FppConstantsAc_constants.out.cpp FppConstantsAc_constants.ref.cpp
}

constants_guard()
{
  run_test "-g GUARD_PREFIX -p $PWD" constants && \
  remove_year < FppConstantsAc.hpp > FppConstantsAc_constants_guard.out.hpp && \
  diff -u FppConstantsAc_constants_guard.out.hpp FppConstantsAc_constants_guard.ref.hpp && \
  remove_year < FppConstantsAc.cpp > FppConstantsAc_constants_guard.out.cpp && \
  diff -u FppConstantsAc_constants_guard.out.cpp FppConstantsAc_constants_guard.ref.cpp
}

constants_output_dir()
{
  run_test "-d output_dir -p $PWD" constants output_dir/constants && \
    remove_year < output_dir/FppConstantsAc.hpp > output_dir/FppConstantsAc.out.hpp && \
    diff output_dir/FppConstantsAc.out.hpp output_dir/FppConstantsAc.ref.hpp && \
    remove_year < output_dir/FppConstantsAc.cpp > output_dir/FppConstantsAc.out.cpp && \
    diff output_dir/FppConstantsAc.out.cpp output_dir/FppConstantsAc.ref.cpp
}

constants_string()
{
  run_test "-p $PWD" constants_string constants_string && \
    remove_year < FppConstantsAc.hpp > FppConstantsAc_constants_string.out.hpp && \
    diff -u FppConstantsAc_constants_string.out.hpp FppConstantsAc_constants_string.ref.hpp && \
    remove_year < FppConstantsAc.cpp > FppConstantsAc_constants_string.out.cpp && \
    diff -u FppConstantsAc_constants_string.out.cpp FppConstantsAc_constants_string.ref.cpp
}

run_suite $tests
