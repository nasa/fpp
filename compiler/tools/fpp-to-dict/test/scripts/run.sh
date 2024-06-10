#!/bin/sh

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
  $fpp_to_dict $args $infile.fpp 2>&1 | remove_author | remove_path_prefix > $outfile.out.txt
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
  if python3 -c "import jsonschema" > /dev/null 2>&1; then
    result=`python3 ../python/json_schema_validator.py --json_dict $dictFile'TopologyDictionary.json' --schema ../dictionary.schema.json`
    if [ "$result" != "Dictionary JSON is valid!" ]; 
    then
      echo "\n"$result 1>&2
      return 1
    fi
  else
    # Warn, but don't fail the tests, if schema validation is not installed
    echo "json schema valididation is not installed; skipping" 1>&2
  fi
}

. ./run.sh
run_suite $tests
