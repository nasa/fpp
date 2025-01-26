#!/bin/sh -e

# ----------------------------------------------------------------------
# defs.sh
# ----------------------------------------------------------------------

export LEVEL=../..
. $LEVEL/defs.sh

redo-ifchange defs.sh

export FILES="
index.adoc
"
