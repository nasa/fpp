#!/bin/sh -e

exec 1>&2

redo-ifchange $2
zip -r $3 $2
