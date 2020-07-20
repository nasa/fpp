#!/bin/sh -e

# ----------------------------------------------------------------------
# refresh.do
# Refresh docs
# ----------------------------------------------------------------------

redo-ifchange spec/fpp-spec.html
cp spec/fpp-spec.html index.html
