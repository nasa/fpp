tests="
bad_channel
bad_omit_channel
duplicate_id_explicit
duplicate_id_implicit
duplicate_packet_name
id_not_numeric
instance_not_defined
instance_not_in_topology
level_not_numeric
level_out_of_range
negative_id
negative_level
ok
omit_instance_not_defined
omit_instance_not_in_topology
"

ok()
{
  run_test instances.fpp ok
}
