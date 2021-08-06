#!/bin/sh -e

# ----------------------------------------------------------------------
# refresh.do
# Refresh docs
# ----------------------------------------------------------------------

index=index/index.html
spec=spec/fpp-spec.html
users_guide=users-guide/fpp-users-guide.html
files="$index $spec $users_guide"
redo-ifchange $files
for file in $files
do
  cp $file .
done
