#!/bin/sh -e

# ----------------------------------------------------------------------
# defs.sh
# ----------------------------------------------------------------------

export LEVEL=../..
. $LEVEL/defs.sh

redo-ifchange defs.sh

export FILES="
Introduction.adoc
Installing-FPP.adoc
Defining-Constants.adoc
Writing-Comments-and-Annotations.adoc
Defining-Modules.adoc
Defining-Types.adoc
Defining-Enums.adoc
Defining-Ports.adoc
Specifying-Models.adoc
Analyzing-and-Translating-Models.adoc
"
