#!/bin/sh -e

# ----------------------------------------------------------------------
# keywords.txt.do
# ----------------------------------------------------------------------

redo-ifchange fpp-spec.adoc
awk '{ for (i = 1; i <= NF; ++i) { if ($i ~ /^`[A-Za-z_]*`$/ && length($i) > 3) print $i } }' fpp-spec.adoc | \
  sed 's/`//g' | sort | uniq
