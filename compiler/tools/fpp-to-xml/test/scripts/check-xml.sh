#!/bin/sh -e

# ----------------------------------------------------------------------
# Compile ref XML files, to check them for validity
# ----------------------------------------------------------------------

pwd=$PWD

fprime_codegen=$COMPILER_ROOT/scripts/fprime-codegen
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
  # Skip XML to C++ in cases where the F Prime Python autocoder is
  # broken
  var='SKIP_XML_FOR_'$base
  skip_xml_cmd='echo $'$var
  xml_file=$base'Ai.xml'
  if test -z "`eval $skip_xml_cmd`"
  then
    cd $dir
    echo "compiling $xml_file"
    $fprime_codegen $xml_file > /dev/null
    cd $pwd
  else
    echo "skipping $xml_file"
  fi
done
