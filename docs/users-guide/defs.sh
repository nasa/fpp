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
Defining-State-Machines.adoc
Defining-Components.adoc
Defining-Component-Instances.adoc
Defining-Topologies.adoc
Specifying-Models-as-Files.adoc
Analyzing-and-Translating-Models.adoc
Writing-C-Plus-Plus-Implementations.adoc
"
