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
  if test $# -ne 1
  then
    echo 'usage: move_json file' 1>&2
    exit 1
  fi
  file=$1
  for suffix in json
  do
    mv $file'TopologyDictionary.'$suffix $file'TopologyDictionary.ref.'$suffix
  done
}

. ./update-ref.sh

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
