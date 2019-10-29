#!/bin/sh -e

# ----------------------------------------------------------------------
# used-tags.txt.do
# ----------------------------------------------------------------------

redo-ifchange fpp-spec.adoc

awk '/<</ {
  n = split($0, fields, /<</)
  for (i = 2; i <= n; ++i) {
    field = fields[i]
    sub(/,.*/, "", field)
    print field
  }
}' fpp-spec.adoc | sort | uniq
