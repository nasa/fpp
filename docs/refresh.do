#!/bin/sh -e

# ----------------------------------------------------------------------
# refresh.do
# Refresh docs
# ----------------------------------------------------------------------

redo-ifchange ../spec/fpp-spec.html
cp ../spec/fpp-spec.html index.html
cp ../spec/code-prettify/run_prettify.js code-prettify
