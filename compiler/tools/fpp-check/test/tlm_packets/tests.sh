tests="
bad_channel
instance_not_defined
instance_not_in_topology
level_not_numeric
negative_level
ok
"

ok()
{
  run_test instances.fpp ok
}
