basic()
{
  update "-p $PWD" Basic
  move_cpp BasicStateMachine
}

choice()
{
  update "-p $PWD" Junction
  move_cpp JunctionStateMachine
}

nested()
{
  update "-p $PWD" Nested
  move_cpp NestedStateMachine
}
