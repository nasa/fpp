#!/bin/sh -e

# ----------------------------------------------------------------------
# defs.sh
# ----------------------------------------------------------------------

export LEVEL=../..
. $LEVEL/defs.sh

redo-ifchange defs.sh

export FILES="
Introduction.adoc
Syntax-Notation.adoc
Lexical-Elements.adoc
Element-Sequences.adoc
Definitions/Definitions.adoc
State-Machine-Behavior-Elements/State-Machine-Behavior-Elements.adoc
Specifiers/Specifiers.adoc
Port-Instance-Identifiers.adoc
Type-Names.adoc
Expressions/Expressions.adoc
Formal-Parameter-Lists.adoc
Format-Strings.adoc
Comments-and-Annotations.adoc
Translation-Units-and-Models.adoc
Scoping-of-Names.adoc
Definitions-and-Uses.adoc
Types.adoc
Type-Checking.adoc
Values.adoc
Evaluation.adoc
Analysis-and-Translation.adoc
"
