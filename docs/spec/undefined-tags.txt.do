#!/bin/sh -e

# ----------------------------------------------------------------------
# undefined-tags.txt.do
# ----------------------------------------------------------------------

redo-ifchange defined-tags.txt used-tags.txt
diff -u defined-tags.txt used-tags.txt | grep '^+' | grep -v '++' | sed 's/^\+//' > $3

