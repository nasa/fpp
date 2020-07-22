#!/bin/sh -e

# ----------------------------------------------------------------------
# undefined-tags.annotated.txt.do
# ----------------------------------------------------------------------

redo-ifchange undefined-tags.txt
for tag in `cat undefined-tags.txt`
do
  echo $tag
  grep -rn --include '*.adoc' '<<'$tag',' * | grep -v fpp-users-guide.adoc | sed 's/^/  /'
done > $3

if test `awk 'END { print NR }' $3` -gt 0
then
  echo '=== There are undefined tags ===' 1>&2
  cat $3 1>&2
fi
