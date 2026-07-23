#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_dict=$COMPILER_ROOT/bin/fpp-to-dict

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
  $fpp_to_dict $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.ref.txt
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

move_json()
{
  for file in "$@"
  do
    mv $file'TopologyDictionary.json' $file'TopologyDictionary.ref.json'
  done
}

move_system_json()
{
  for file in "$@"
  do
    mv $file'SystemDictionary.json' $file'SystemDictionary.ref.json'
  done
}

. ./update-ref.sh

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
