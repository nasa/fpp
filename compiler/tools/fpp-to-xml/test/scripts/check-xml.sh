#!/bin/sh -e

# ----------------------------------------------------------------------
# Compile ref XML files, to check them for validity
# ----------------------------------------------------------------------
# 1. Compile from XML to C++
# 2. Compile to C++ where feasible. Skip it if the F Prime dependencies
#    are too complicated.
# ----------------------------------------------------------------------

pwd=$PWD

if test `uname` = Darwin
then
export FPRIME_GCC_FLAGS="
$FPRIME_GCC_FLAGS
-Wno-gnu-zero-variadic-macro-arguments
-Wno-unused-parameter
"
else
export FPRIME_GCC_FLAGS="
$FPRIME_GCC_FLAGS
-Wno-variadic-macros
-Wno-unused-parameter
"
fi
fprime_codegen=$COMPILER_ROOT/scripts/fprime-codegen
fprime_gcc=$COMPILER_ROOT/scripts/fprime-gcc
test_dir="$COMPILER_ROOT/tools/fpp-to-xml/test"

files=`find . -name '*Ai.ref.xml'`

for file in $files
do
  dir=`dirname $file`
  base=`basename $file Ai.ref.xml`
  xml_file=$base'Ai.xml'
  echo "copying $file to $xml_file"
  cp $file $dir/$xml_file
done

for file in $files
do
  dir=`dirname $file`
  base=`basename $file Ai.ref.xml`
  xml_file=$base'Ai.xml'
  cd $dir
  echo "compiling $xml_file"
  $fprime_codegen $xml_file > /dev/null
  cd $pwd
done

for file in $files
do
  dir=`dirname $file`
  base=`basename $file Ai.ref.xml`
  var='SKIP_CPP_FOR_'$base
  skip_cpp_cmd='echo $'$var
  cpp_file=$base'Ac.cpp'
  if test -z "`eval $skip_cpp_cmd`"
    then
    echo "compiling $cpp_file"
    cd $dir
    $fprime_gcc -I $test_dir -c $cpp_file
    cd $pwd
  else
    echo "skipping $cpp_file"
  fi
done
