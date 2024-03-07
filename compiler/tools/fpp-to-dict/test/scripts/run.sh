#!/bin/bash

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_dict=$COMPILER_ROOT/bin/fpp-to-dict

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
  $fpp_to_dict $args $infile.fpp 2>&1
}

. ./tests.sh

# Default tests
for t in $tests
do
  echo "
$t()
{
  run_test $t
}"
done > default-tests.sh
. ./default-tests.sh

diff_json()
{
  for file in $@
  do
    if ! diff $file'TopologyDictionary.json' $file'TopologyDictionary.ref.json'
    then
      return 1
    fi
  done
}

validate_json_schema()
{ 
  dictFile=$1
  # Check to see if the dictionary JSON schema is valid
  if which python3 > /dev/null 2>&1
  then
    result=$(python3 ../python/json_schema_validator.py --json_dict $dictFile'TopologyDictionary.json' --schema ../dictionary.schema.json)
    if [[ ! $result = *"is valid"* ]]; 
    then
      echo "\n"$result 1>&2
      return 1
    fi
  else
    # Work around an issue in CI
    echo "python3 is not available; skipping JSON schema validation" 1>&2
  fi
}

. ./run.sh
run_suite $tests
