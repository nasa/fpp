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

basic_guard_string()
{
  update "$fpp_flags" BasicGuardString
  move_cpp BasicGuardStringStateMachine
}

basic_guard_test_abs_type()
{
  update "$fpp_flags" BasicGuardTestAbsType
  move_cpp BasicGuardTestAbsTypeStateMachine
}

basic_guard_test_array()
{
  update "$fpp_flags" BasicGuardTestArray
  move_cpp BasicGuardTestArrayStateMachine
}

basic_guard_test_enum()
{
  update "$fpp_flags" BasicGuardTestEnum
  move_cpp BasicGuardTestEnumStateMachine
}

basic_guard_test_struct()
{
  update "$fpp_flags" BasicGuardTestStruct
  move_cpp BasicGuardTestStructStateMachine
}

basic_guard_u32()
{
  update "$fpp_flags" BasicGuardU32
  move_cpp BasicGuardU32StateMachine
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
  update "$fpp_flags" BasicTestStruct
  move_cpp BasicTestStructStateMachine
}

basic_u32()
{
  update "$fpp_flags" BasicU32
  move_cpp BasicU32StateMachine
}

internal()
{
  update "$fpp_flags" Internal
  move_cpp InternalStateMachine
}

polymorphism()
{
  update "$fpp_flags" Polymorphism
  move_cpp PolymorphismStateMachine
}

state_to_child()
{
  update "$fpp_flags" StateToChild
  move_cpp StateToChildStateMachine
}

state_to_choice()
{
  update "$fpp_flags" StateToChoice 1>&2
  move_cpp StateToChoiceStateMachine
}

state_to_self()
{
  update "$fpp_flags" StateToSelf
  move_cpp StateToSelfStateMachine
}

state_to_state()
{
  update "$fpp_flags" StateToState
  move_cpp StateToStateStateMachine
}
