#!/bin/sh -e

redo-ifchange index.adoc
ispell index.adoc 1>&2
asciidoctor -n index.adoc -o $3
