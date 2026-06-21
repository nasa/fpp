basic()
{
  run_test "-p $PWD" Basic && \
    diff_cpp BasicStateMachine && \
    diff_cpp Basic_StateEnum
}

choice()
{
  run_test "-p $PWD" Choice && \
    diff_cpp ChoiceStateMachine && \
    diff_cpp Choice_StateEnum
}

nested()
{
  run_test "-p $PWD" Nested && \
    diff_cpp NestedStateMachine && \
    diff_cpp Nested_StateEnum
}
