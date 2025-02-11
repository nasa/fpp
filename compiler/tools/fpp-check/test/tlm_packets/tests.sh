tests="
bad_channel
duplicate_id_explicit
duplicate_id_implicit
id_not_numeric
instance_not_defined
instance_not_in_topology
level_not_numeric
level_out_of_range
negative_id
negative_level
ok
"

ok()
{
  run_test instances.fpp ok
}
