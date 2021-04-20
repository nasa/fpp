#!/bin/sh -e

. $COMPILER_ROOT/scripts/utils.sh

clean
rm -rf default-tests.sh default-update-ref.sh DefaultDict
for file in `find . -name '*Ai.xml' -or -name '*Ac.*' -or -name '*.names.txt' -or -name '*.csv'`
do
  rm $file
done
