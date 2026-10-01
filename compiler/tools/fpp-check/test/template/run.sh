#!/bin/sh

# Tests that also check the unconnected port report. The report makes the
# instances and connections of an expanded topology observable.

import_two_expansions_ok()
{
  run_test "-u import_two_expansions_ok-unconnected.out.txt" import_two_expansions_ok && \
    diff -u import_two_expansions_ok-unconnected.out.txt import_two_expansions_ok-unconnected.ref.txt
}

interfaces_pattern_explicit_target_ok()
{
  run_test "-u interfaces_pattern_explicit_target_ok-unconnected.out.txt" interfaces_pattern_explicit_target_ok && \
    diff -u interfaces_pattern_explicit_target_ok-unconnected.out.txt interfaces_pattern_explicit_target_ok-unconnected.ref.txt
}

interfaces_topology_arg_ok()
{
  run_test "-u interfaces_topology_arg_ok-unconnected.out.txt" interfaces_topology_arg_ok && \
    diff -u interfaces_topology_arg_ok-unconnected.out.txt interfaces_topology_arg_ok-unconnected.ref.txt
}

interfaces_topology_arg_order_ok()
{
  run_test "-u interfaces_topology_arg_order_ok-unconnected.out.txt" interfaces_topology_arg_order_ok && \
    diff -u interfaces_topology_arg_order_ok-unconnected.out.txt interfaces_topology_arg_order_ok-unconnected.ref.txt
}
