. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" Basic && \
    diff_cpp BasicStateMachine
}

basic_guard()
{
  run_test "$fpp_flags" BasicGuard && \
    diff_cpp BasicGuardStateMachine
}

basic_guard_string()
{
  run_test "$fpp_flags" BasicGuardString && \
    diff_cpp BasicGuardStringStateMachine
}

basic_guard_test_abs_type()
{
  run_test "$fpp_flags" BasicGuardTestAbsType && \
    diff_cpp BasicGuardTestAbsTypeStateMachine
}

basic_guard_test_array()
{
  run_test "$fpp_flags" BasicGuardTestArray && \
    diff_cpp BasicGuardTestArrayStateMachine
}

basic_guard_test_enum()
{
  run_test "$fpp_flags" BasicGuardTestEnum && \
    diff_cpp BasicGuardTestEnumStateMachine
}

basic_guard_test_struct()
{
  run_test "$fpp_flags" BasicGuardTestStruct && \
    diff_cpp BasicGuardTestStructStateMachine
}

basic_guard_u32()
{
  run_test "$fpp_flags" BasicGuardU32 && \
    diff_cpp BasicGuardU32StateMachine
}

basic_internal()
{
  run_test "$fpp_flags" BasicInternal && \
    diff_cpp BasicInternalStateMachine
}

basic_self()
{
  run_test "$fpp_flags" BasicSelf && \
    diff_cpp BasicSelfStateMachine
}

basic_string()
{
  run_test "$fpp_flags" BasicString && \
    diff_cpp BasicStringStateMachine
}

basic_test_abs_type()
{
  run_test "$fpp_flags" BasicTestAbsType && \
    diff_cpp BasicTestAbsTypeStateMachine
}

basic_test_array()
{
  run_test "$fpp_flags" BasicTestArray && \
    diff_cpp BasicTestArrayStateMachine
}

basic_test_enum()
{
  run_test "$fpp_flags" BasicTestEnum && \
    diff_cpp BasicTestEnumStateMachine
}

basic_test_struct()
{
  run_test "$fpp_flags" BasicTestStruct && \
    diff_cpp BasicTestStructStateMachine
}

basic_u32()
{
  run_test "$fpp_flags" BasicU32 && \
    diff_cpp BasicU32StateMachine
}

internal()
{
  run_test "$fpp_flags" Internal && \
    diff_cpp InternalStateMachine
}

polymorphism()
{
  run_test "$fpp_flags" Polymorphism && \
    diff_cpp PolymorphismStateMachine
}

state_to_child()
{
  run_test "$fpp_flags" StateToChild && \
    diff_cpp StateToChildStateMachine
}

state_to_choice()
{
  run_test "$fpp_flags" StateToChoice && \
    diff_cpp StateToChoiceStateMachine
}

state_to_self()
{
  run_test "$fpp_flags" StateToSelf && \
    diff_cpp StateToSelfStateMachine
}

state_to_state()
{
  run_test "$fpp_flags" StateToState && \
    diff_cpp StateToStateStateMachine
}
