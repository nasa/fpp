#!/bin/sh -e

# ----------------------------------------------------------------------
# all.do
# ----------------------------------------------------------------------

exec 1>&2

export GREEN='\033[32m'
export NO_COLOR='\033[0m'
export RED='\033[31m'

fpp_format=../../../tools/fpp-format/fpp-format
redo-ifchange $fpp_format
num_failed=0

files=`ls ../../fpp-syntax/ok/*.fpp`

test_file ()
{
  base=`basename $file .fpp`
  $fpp_format $file > $base.1.fpp
  $fpp_format $base.1.fpp > $base.2.fpp
  diff $base.1.fpp $base.2.fpp > $base.fpp.out 2>&1
}

redo-always

for file in $files
do
  base=`basename $file`
  printf '%-50s' $base
  if test_file $file
  then
    echo $GREEN'PASSED'$NO_COLOR
  else
    echo $RED'FAILED'$NO_COLOR
    num_failed=`expr $num_failed + 1`
  fi
done

exit $num_failed

