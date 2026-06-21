. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" Basic && \
    diff_cpp BasicStateMachine && \
    diff_cpp Basic_StateEnum
}

basic_guard()
{
  run_test "$fpp_flags" BasicGuard && \
    diff_cpp BasicGuardStateMachine && \
    diff_cpp BasicGuard_StateEnum
}

basic_guard_string()
{
  run_test "$fpp_flags" BasicGuardString && \
    diff_cpp BasicGuardStringStateMachine && \
    diff_cpp BasicGuardString_StateEnum
}

basic_guard_test_abs_type()
{
  run_test "$fpp_flags" BasicGuardTestAbsType && \
    diff_cpp BasicGuardTestAbsTypeStateMachine && \
    diff_cpp BasicGuardTestAbsType_StateEnum
}

basic_guard_test_array()
{
  run_test "$fpp_flags" BasicGuardTestArray && \
    diff_cpp BasicGuardTestArrayStateMachine && \
    diff_cpp BasicGuardTestArray_StateEnum
}

basic_guard_test_enum()
{
  run_test "$fpp_flags" BasicGuardTestEnum && \
    diff_cpp BasicGuardTestEnumStateMachine && \
    diff_cpp BasicGuardTestEnum_StateEnum
}

basic_guard_test_struct()
{
  run_test "$fpp_flags" BasicGuardTestStruct && \
    diff_cpp BasicGuardTestStructStateMachine && \
    diff_cpp BasicGuardTestStruct_StateEnum
}

basic_guard_u32()
{
  run_test "$fpp_flags" BasicGuardU32 && \
    diff_cpp BasicGuardU32StateMachine && \
    diff_cpp BasicGuardU32_StateEnum
}

basic_internal()
{
  run_test "$fpp_flags" BasicInternal && \
    diff_cpp BasicInternalStateMachine && \
    diff_cpp BasicInternal_StateEnum
}

basic_self()
{
  run_test "$fpp_flags" BasicSelf && \
    diff_cpp BasicSelf_StateEnum && \
    diff_cpp BasicSelfStateMachine
}

basic_string()
{
  run_test "$fpp_flags" BasicString && \
    diff_cpp BasicString_StateEnum && \
    diff_cpp BasicStringStateMachine
}

basic_test_abs_type()
{
  run_test "$fpp_flags" BasicTestAbsType && \
    diff_cpp BasicTestAbsType_StateEnum && \
    diff_cpp BasicTestAbsTypeStateMachine
}

basic_test_array()
{
  run_test "$fpp_flags" BasicTestArray && \
    diff_cpp BasicTestArray_StateEnum && \
    diff_cpp BasicTestArrayStateMachine
}

basic_test_enum()
{
  run_test "$fpp_flags" BasicTestEnum && \
    diff_cpp BasicTestEnumStateMachine && \
    diff_cpp BasicTestEnum_StateEnum
}

basic_test_struct()
{
  run_test "$fpp_flags" BasicTestStruct && \
    diff_cpp BasicTestStructStateMachine && \
    diff_cpp BasicTestStruct_StateEnum
}

basic_u32()
{
  run_test "$fpp_flags" BasicU32 && \
    diff_cpp BasicU32StateMachine && \
    diff_cpp BasicU32_StateEnum
}

internal()
{
  run_test "$fpp_flags" Internal && \
    diff_cpp InternalStateMachine && \
    diff_cpp Internal_StateEnum
}

polymorphism()
{
  run_test "$fpp_flags" Polymorphism && \
    diff_cpp PolymorphismStateMachine && \
    diff_cpp Polymorphism_StateEnum
}

state_to_child()
{
  run_test "$fpp_flags" StateToChild && \
    diff_cpp StateToChildStateMachine && \
    diff_cpp StateToChild_StateEnum
}

state_to_choice()
{
  run_test "$fpp_flags" StateToChoice && \
    diff_cpp StateToChoiceStateMachine && \
    diff_cpp StateToChoice_StateEnum
}

state_to_self()
{
  run_test "$fpp_flags" StateToSelf && \
    diff_cpp StateToSelfStateMachine && \
    diff_cpp StateToSelf_StateEnum
}

state_to_state()
{
  run_test "$fpp_flags" StateToState && \
    diff_cpp StateToStateStateMachine && \
    diff_cpp StateToState_StateEnum
}
