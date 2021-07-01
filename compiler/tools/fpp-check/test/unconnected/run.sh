#!/bin/sh

basic()
{
  run_test "-u basic-unconnected.out.txt" basic && \
    diff -u basic-unconnected.out.txt basic-unconnected.ref.txt
}

internal()
{
  run_test "-u internal-unconnected.out.txt" internal && \
    diff -u internal-unconnected.out.txt internal-unconnected.ref.txt
}
