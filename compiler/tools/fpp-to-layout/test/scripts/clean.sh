#!/bin/sh -e

. $COMPILER_ROOT/scripts/utils.sh

clean
rm -rf default-tests.sh default-update-ref.sh

for file in `find . -name '*.out.txt'`
do
  rm $file
done

for dir in `find . -type d -name '*Layout'`
do
  rm -r $dir
done
