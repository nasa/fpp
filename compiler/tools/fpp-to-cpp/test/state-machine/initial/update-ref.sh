basic()
{
  update "-p $PWD" Basic
  move_cpp BasicStateMachine
}

choice()
{
  update "-p $PWD" Choice
  move_cpp ChoiceStateMachine
}

nested()
{
  update "-p $PWD" Nested
  move_cpp NestedStateMachine
}
