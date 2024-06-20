#!/bin/sh -e

cd `dirname $0`

redo-ifchange ../../Writing-C-Plus-Plus-Implementations.adoc

awk '
/\/\/ A minimal implementation/ { output = 1 }
output == 1 { print }
/#endif/ { output = 0 }
' ../../Writing-C-Plus-Plus-Implementations.adoc > $3
