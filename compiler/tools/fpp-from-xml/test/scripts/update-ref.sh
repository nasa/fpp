#!/bin/sh

export LOCAL_PATH_PREFIX=`cd $COMPILER_ROOT; echo $PWD`

. $COMPILER_ROOT/scripts/test-utils.sh

fpp_from_xml=$COMPILER_ROOT/bin/fpp-from-xml

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
  $fpp_from_xml $args $infile.xml 2>&1 | remove_path_prefix > $outfile.ref.txt
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

if test -f update-ref.sh
then
  . ./update-ref.sh
fi

for t in $tests
do
  echo "updating ref output for $t"
  $t
done
