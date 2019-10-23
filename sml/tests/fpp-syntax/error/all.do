#!/bin/sh -e

# ----------------------------------------------------------------------
# all.do
# ----------------------------------------------------------------------

exec 1>&2

redo-always

export GREEN='\033[32m'
export NO_COLOR='\033[0m'
export RED='\033[31m'

fpp_syntax=../../../tools/fpp-syntax/fpp-syntax
redo-ifchange $fpp_syntax 2>&1
num_failed=0

for file in `ls *.fpp`
do
  printf '%-50s' $file
  if ! $fpp_syntax $file > $file.out 2>&1
  then
    echo $GREEN'PASSED'$NO_COLOR
  else
    echo $RED'FAILED'$NO_COLOR
    num_failed=`expr $num_failed + 1`
  fi
done

exit $num_failed

