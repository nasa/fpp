#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_cpp=$COMPILER_ROOT/bin/fpp-to-cpp

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
  $fpp_to_cpp $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.ref.txt
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

move_cpp()
{
  file=$1
  target_suffix=$2
  for suffix in hpp cpp
  do
    cp $file'Ac.'$suffix $file'Ac'$target_suffix'.ref.'$suffix
  done
}

. ./update-ref.sh

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
