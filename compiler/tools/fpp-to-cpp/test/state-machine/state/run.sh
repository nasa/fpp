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
  run_test "$fpp_flags" basic_test_struct && \
    diff_cpp BasicTestStructStateMachine
}

basic_u32()
{
  run_test "$fpp_flags" basic_u32 && \
    diff_cpp BasicU32StateMachine
}

internal()
{
  run_test "$fpp_flags" internal && \
    diff_cpp InternalStateMachine
}

polymorphism()
{
  run_test "$fpp_flags" polymorphism && \
    diff_cpp PolymorphismStateMachine
}

state_to_child()
{
  run_test "$fpp_flags" state_to_child && \
    diff_cpp StateToChildStateMachine
}

state_to_junction()
{
  run_test "$fpp_flags" state_to_junction && \
    diff_cpp StateToJunctionStateMachine
}

state_to_self()
{
  run_test "$fpp_flags" state_to_self && \
    diff_cpp StateToSelfStateMachine
}

state_to_state()
{
  run_test "$fpp_flags" state_to_state && \
    diff_cpp StateToStateStateMachine
}
