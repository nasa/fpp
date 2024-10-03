basic()
{
  update "-p $PWD" Basic
  move_cpp BasicStateMachine
}

junction()
{
  update "-p $PWD" Junction
  move_cpp JunctionStateMachine
}

nested()
{
  update "-p $PWD" Nested
  move_cpp NestedStateMachine
}
