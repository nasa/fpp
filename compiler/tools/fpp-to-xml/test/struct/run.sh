#!/bin/sh

. ../../../../scripts/test-utils.sh

fpp_to_xml=../../../../bin/fpp-to-xml

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

enum()
{
  run_test '' enum && \
    diff EEnumAi.xml EEnumAi.ref.xml
}

struct_abs_type()
{
  run_test "-p $PWD" struct_abs_type && \
  diff -u StructAbsTypeSerializableAi.xml StructAbsTypeSerializableAi.ref.xml
}

struct_default()
{
  run_test "-p $PWD" struct_default && \
  diff -u StructDefaultSerializableAi.xml StructDefaultSerializableAi.ref.xml
}

struct_enum_member()
{
  run_test "-i enum.fpp -p $PWD" struct_enum_member && \
  diff -u StructEnumMemberSerializableAi.xml StructEnumMemberSerializableAi.ref.xml
}

struct_ok()
{
  run_test "-n struct_ok.names.txt -p $PWD" struct_ok && \
    diff -u struct_ok.names.txt struct_ok.names.ref.txt && \
    diff -u StructOK1SerializableAi.xml StructOK1SerializableAi.ref.xml && \
    diff -u StructOK2SerializableAi.xml StructOK2SerializableAi.ref.xml
}

struct_format()
{
  run_test '' struct_format && \
    diff StructFormatSerializableAi.xml StructFormatSerializableAi.ref.xml
}

struct_modules()
{
  run_test "-p $PWD" struct_modules && \
    diff StructModules1SerializableAi.xml StructModules1SerializableAi.ref.xml && \
    diff StructModules2SerializableAi.xml StructModules2SerializableAi.ref.xml && \
    diff StructModules3SerializableAi.xml StructModules3SerializableAi.ref.xml
}

struct_output_dir()
{
  run_test '-d output_dir' output_dir/struct_output_dir && \
    diff output_dir/StructOutputDirSerializableAi.xml output_dir/StructOutputDirSerializableAi.ref.xml
}

struct_string()
{
  run_test '' struct_string && \
    diff StructStringSerializableAi.xml StructStringSerializableAi.ref.xml
}

struct_string_array()
{
  run_test '' struct_string_array && \
    diff StructStringArraySerializableAi.xml StructStringArraySerializableAi.ref.xml
}

run_suite $tests
