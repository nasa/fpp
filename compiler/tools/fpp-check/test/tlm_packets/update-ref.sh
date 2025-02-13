#!/bin/sh

for t in $tests
do
  echo "
$t()
{
  update instances.fpp $t
}"
done > default-update-ref.sh
. ./default-update-ref.sh
