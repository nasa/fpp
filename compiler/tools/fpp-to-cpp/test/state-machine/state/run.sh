basic()
{
  run_test "-p $PWD" basic && \
    diff_cpp BasicStateMachine
}

basic_self()
{
  run_test "-p $PWD" basic_self && \
    diff_cpp BasicSelfStateMachine
}
