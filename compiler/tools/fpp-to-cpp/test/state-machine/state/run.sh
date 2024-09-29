. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" basic && \
    diff_cpp BasicStateMachine
}

basic_guard()
{
  run_test "$fpp_flags" basic_guard && \
    diff_cpp BasicGuardStateMachine
}

basic_internal()
{
  run_test "$fpp_flags" basic_internal && \
    diff_cpp BasicInternalStateMachine
}

basic_self()
{
  run_test "$fpp_flags" basic_self && \
    diff_cpp BasicSelfStateMachine
}

basic_string()
{
  run_test "$fpp_flags" basic_string && \
    diff_cpp BasicStringStateMachine
}

basic_test_abs_type()
{
  run_test "$fpp_flags" basic_test_abs_type && \
    diff_cpp BasicTestAbsTypeStateMachine
}

basic_test_array()
{
  run_test "$fpp_flags" basic_test_array && \
    diff_cpp BasicTestArrayStateMachine
}

basic_test_enum()
{
  run_test "$fpp_flags" basic_test_enum && \
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
