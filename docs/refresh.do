#!/bin/sh -e

# ----------------------------------------------------------------------
# refresh.do
# Refresh docs
# ----------------------------------------------------------------------

spec=spec/fpp-spec.html
users_guide=users-guide/fpp-users-guide.html
redo-ifchange $spec $users_guide
cp $spec index.html
cp $users_guide .
