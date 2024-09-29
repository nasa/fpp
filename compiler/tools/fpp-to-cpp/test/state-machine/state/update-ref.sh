. ./fpp-flags.sh

basic()
{
  update "$fpp_flags" basic
  move_cpp BasicStateMachine
}

basic_guard()
{
  update "$fpp_flags" basic_guard
  move_cpp BasicGuardStateMachine
}

basic_internal()
{
  update "$fpp_flags" basic_internal
  move_cpp BasicInternalStateMachine
}

basic_self()
{
  update "$fpp_flags" basic_self
  move_cpp BasicSelfStateMachine
}

basic_string()
{
  update "$fpp_flags" basic_string
  move_cpp BasicStringStateMachine
}

basic_test_abs_type()
{
  update "$fpp_flags" basic_test_abs_type
  move_cpp BasicTestAbsTypeStateMachine
}

basic_test_array()
{
  update "$fpp_flags" basic_test_array
  move_cpp BasicTestArrayStateMachine
}

basic_test_enum()
{
  update "$fpp_flags" basic_test_enum
  move_cpp BasicTestEnumStateMachine
}

basic_test_struct()
{
  update "$fpp_flags" basic_test_struct
  move_cpp BasicTestStructStateMachine
}

basic_u32()
{
  update "$fpp_flags" basic_u32
  move_cpp BasicU32StateMachine
}

state_to_junction()
{
  update "$fpp_flags" state_to_junction
  move_cpp StateToJunctionStateMachine
}

state_to_self()
{
  update "$fpp_flags" state_to_self
  move_cpp StateToSelfStateMachine
}

state_to_state()
{
  update "$fpp_flags" state_to_state
  move_cpp StateToStateStateMachine
}
