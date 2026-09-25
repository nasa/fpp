#!/bin/sh

import_two_expansions_ok()
{
  update "-u import_two_expansions_ok-unconnected.out.txt" import_two_expansions_ok
  mv import_two_expansions_ok-unconnected.out.txt import_two_expansions_ok-unconnected.ref.txt
}

interfaces_pattern_explicit_target_ok()
{
  update "-u interfaces_pattern_explicit_target_ok-unconnected.out.txt" interfaces_pattern_explicit_target_ok
  mv interfaces_pattern_explicit_target_ok-unconnected.out.txt interfaces_pattern_explicit_target_ok-unconnected.ref.txt
}

interfaces_topology_arg_ok()
{
  update "-u interfaces_topology_arg_ok-unconnected.out.txt" interfaces_topology_arg_ok
  mv interfaces_topology_arg_ok-unconnected.out.txt interfaces_topology_arg_ok-unconnected.ref.txt
}

interfaces_topology_arg_order_ok()
{
  update "-u interfaces_topology_arg_order_ok-unconnected.out.txt" interfaces_topology_arg_order_ok
  mv interfaces_topology_arg_order_ok-unconnected.out.txt interfaces_topology_arg_order_ok-unconnected.ref.txt
}
