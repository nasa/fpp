. ./fpp-flags.sh

basic()
{
  update "$fpp_flags" Basic && \
  move_cpp BasicStateMachine && \
  move_cpp Basic_StateEnum
}

basic_guard()
{
  update "$fpp_flags" BasicGuard && \
  move_cpp BasicGuardStateMachine && \
  move_cpp BasicGuard_StateEnum
}

basic_guard_string()
{
  update "$fpp_flags" BasicGuardString && \
  move_cpp BasicGuardStringStateMachine && \
  move_cpp BasicGuardString_StateEnum
}

basic_guard_test_abs_type()
{
  update "$fpp_flags" BasicGuardTestAbsType && \
  move_cpp BasicGuardTestAbsTypeStateMachine && \
  move_cpp BasicGuardTestAbsType_StateEnum
}

basic_guard_test_array()
{
  update "$fpp_flags" BasicGuardTestArray && \
  move_cpp BasicGuardTestArrayStateMachine && \
  move_cpp BasicGuardTestArray_StateEnum
}

basic_guard_test_enum()
{
  update "$fpp_flags" BasicGuardTestEnum && \
  move_cpp BasicGuardTestEnumStateMachine && \
  move_cpp BasicGuardTestEnum_StateEnum
}

basic_guard_test_struct()
{
  update "$fpp_flags" BasicGuardTestStruct && \
  move_cpp BasicGuardTestStructStateMachine && \
  move_cpp BasicGuardTestStruct_StateEnum
}

basic_guard_u32()
{
  update "$fpp_flags" BasicGuardU32 && \
  move_cpp BasicGuardU32StateMachine && \
  move_cpp BasicGuardU32_StateEnum
}

basic_internal()
{
  update "$fpp_flags" BasicInternal && \
  move_cpp BasicInternalStateMachine && \
  move_cpp BasicInternal_StateEnum
}

basic_self()
{
  update "$fpp_flags" BasicSelf && \
  move_cpp BasicSelfStateMachine && \
  move_cpp BasicSelf_StateEnum
}

basic_string()
{
  update "$fpp_flags" BasicString && \
  move_cpp BasicStringStateMachine && \
  move_cpp BasicString_StateEnum
}

basic_test_abs_type()
{
  update "$fpp_flags" BasicTestAbsType && \
  move_cpp BasicTestAbsTypeStateMachine && \
  move_cpp BasicTestAbsType_StateEnum
}

basic_test_array()
{
  update "$fpp_flags" BasicTestArray && \
  move_cpp BasicTestArrayStateMachine && \
  move_cpp BasicTestArray_StateEnum
}

basic_test_enum()
{
  update "$fpp_flags" BasicTestEnum && \
  move_cpp BasicTestEnumStateMachine && \
  move_cpp BasicTestEnum_StateEnum
}

basic_test_struct()
{
  update "$fpp_flags" BasicTestStruct && \
  move_cpp BasicTestStructStateMachine && \
  move_cpp BasicTestStruct_StateEnum
}

basic_u32()
{
  update "$fpp_flags" BasicU32 && \
  move_cpp BasicU32StateMachine && \
  move_cpp BasicU32_StateEnum
}

internal()
{
  update "$fpp_flags" Internal && \
  move_cpp InternalStateMachine && \
  move_cpp Internal_StateEnum
}

polymorphism()
{
  update "$fpp_flags" Polymorphism && \
  move_cpp PolymorphismStateMachine && \
  move_cpp Polymorphism_StateEnum
}

state_to_child()
{
  update "$fpp_flags" StateToChild && \
  move_cpp StateToChildStateMachine && \
  move_cpp StateToChild_StateEnum
}

state_to_choice()
{
  update "$fpp_flags" StateToChoice && \
  move_cpp StateToChoiceStateMachine && \
  move_cpp StateToChoice_StateEnum
}

state_to_self()
{
  update "$fpp_flags" StateToSelf && \
  move_cpp StateToSelfStateMachine && \
  move_cpp StateToSelf_StateEnum
}

state_to_state()
{
  update "$fpp_flags" StateToState && \
  move_cpp StateToStateStateMachine && \
  move_cpp StateToState_StateEnum
}
