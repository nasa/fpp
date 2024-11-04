basic()
{
  run_test "-p $PWD" Basic && \
    diff_cpp BasicStateMachine
}

choice()
{
  run_test "-p $PWD" Junction && \
    diff_cpp JunctionStateMachine
}

nested()
{
  run_test "-p $PWD" Nested && \
    diff_cpp NestedStateMachine
}
