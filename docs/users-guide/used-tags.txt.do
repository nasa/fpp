#!/bin/sh -e

# ----------------------------------------------------------------------
# used-tags.txt.do
# ----------------------------------------------------------------------

redo-ifchange fpp-users-guide.adoc

awk '/<</ {
  n = split($0, fields, /<</)
  for (i = 2; i <= n; ++i) {
    field = fields[i]
    if (field ~ /,/)
      sub(/,.*/, "", field)
    else
      sub(/>>.*/, "", field)
    print field
  }
}' fpp-users-guide.adoc | sort | uniq > $3
