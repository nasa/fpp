basic()
{
  run_test "-p $PWD" basic && \
    diff_cpp BasicStateMachine
}

junction()
{
  run_test "-p $PWD" junction && \
    diff_cpp JunctionStateMachine
}
