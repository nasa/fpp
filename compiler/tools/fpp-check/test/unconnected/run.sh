#!/bin/sh

basic()
{
  run_test "-u basic-unconnected.out.txt" basic && \
    diff -u basic-unconnected.out.txt basic-unconnected.ref.txt
}
