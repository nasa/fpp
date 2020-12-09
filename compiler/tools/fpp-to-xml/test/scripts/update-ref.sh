#!/bin/sh

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_to_xml=$COMPILER_ROOT/bin/fpp-to-xml
fpp_depend=$COMPILER_ROOT/bin/fpp-depend

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
  $fpp_to_xml $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.ref.txt
}

for file in `find . -maxdepth 1 -name '*.ref.txt'`
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
  update '-p '$PWD $t
}"
done > default-update-ref.sh
. ./default-update-ref.sh

move_xml()
{
  for file in $@
  do
    mv $file'Ai.xml' $file'Ai.ref.xml'
  done
}

. ./update-ref.sh

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
