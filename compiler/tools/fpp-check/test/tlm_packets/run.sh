#!/bin/sh

for t in $tests
do
  echo "
$t()
{
  run_test instances.fpp $t
}"
done > default-tests.sh
. ./default-tests.sh
