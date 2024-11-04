basic()
{
  run_test "-p $PWD" Basic && \
    diff_cpp BasicStateMachine
}

choice()
{
  run_test "-p $PWD" Choice && \
    diff_cpp ChoiceStateMachine
}

nested()
{
  run_test "-p $PWD" Nested && \
    diff_cpp NestedStateMachine
}
