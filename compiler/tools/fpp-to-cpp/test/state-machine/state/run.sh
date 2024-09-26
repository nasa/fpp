basic()
{
  run_test "-p $PWD" basic && \
    diff_cpp BasicStateMachine
}

basic_guard()
{
  run_test "-p $PWD" basic_guard && \
    diff_cpp BasicGuardStateMachine
}

basic_self()
{
  run_test "-p $PWD" basic_self && \
    diff_cpp BasicSelfStateMachine
}

basic_u32()
{
  run_test "-p $PWD" basic_u32 && \
    diff_cpp BasicU32StateMachine
}
