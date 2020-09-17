#!/bin/sh -e

# ----------------------------------------------------------------------
# defined-tags.txt.do
# ----------------------------------------------------------------------

redo-ifchange fpp-users-guide.adoc

awk '/^\[\[.*\]\]$/ {
  tag = $0
  sub(/\[\[/, "", tag)
  sub(/\]\]/, "", tag)
  print tag
}
/\[#.*\]/ {
  tag = $0
  sub(/\[#/, "", tag)
  sub(/\]/, "", tag)
  print tag
}' fpp-users-guide.adoc | sort
