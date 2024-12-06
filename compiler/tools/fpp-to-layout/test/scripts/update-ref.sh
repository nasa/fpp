#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_layout=$COMPILER_ROOT/bin/fpp-to-layout

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
  $fpp_to_layout $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.ref.txt
}

move_layout_files()
{
  topDir=$1
  mkdir -p $topDir'Layout.ref'
  for file in ${@: 2}
    do
      mv $topDir'Layout'/$file'.txt' $topDir'Layout.ref'/$file'.txt'
    done
  rm -r $topDir'Layout'
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

. ./update-ref.sh

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
