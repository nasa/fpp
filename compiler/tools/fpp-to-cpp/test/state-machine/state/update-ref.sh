. ./fpp-flags.sh

basic()
{
  update "$fpp_flags" Basic
  move_cpp BasicStateMachine
}

basic_guard()
{
  update "$fpp_flags" BasicGuard
  move_cpp BasicGuardStateMachine
}

basic_internal()
{
  update "$fpp_flags" BasicInternal
  move_cpp BasicInternalStateMachine
}

basic_self()
{
  update "$fpp_flags" BasicSelf
  move_cpp BasicSelfStateMachine
}

basic_string()
{
  update "$fpp_flags" BasicString
  move_cpp BasicStringStateMachine
}

basic_test_abs_type()
{
  update "$fpp_flags" BasicTestAbsType
  move_cpp BasicTestAbsTypeStateMachine
}

basic_test_array()
{
  update "$fpp_flags" BasicTestArray
  move_cpp BasicTestArrayStateMachine
}

basic_test_enum()
{
  update "$fpp_flags" BasicTestEnum
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

internal()
{
  update "$fpp_flags" internal
  move_cpp InternalStateMachine
}

polymorphism()
{
  update "$fpp_flags" polymorphism
  move_cpp PolymorphismStateMachine
}

state_to_child()
{
  update "$fpp_flags" state_to_child
  move_cpp StateToChildStateMachine
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
