basic()
{
  update "-p $PWD" Basic && \
  move_cpp BasicStateMachine && \
  move_cpp Basic_StateEnum
}

choice()
{
  update "-p $PWD" Choice && \
  move_cpp ChoiceStateMachine && \
  move_cpp Choice_StateEnum
}

nested()
{
  update "-p $PWD" Nested && \
  move_cpp NestedStateMachine && \
  move_cpp Nested_StateEnum
}
