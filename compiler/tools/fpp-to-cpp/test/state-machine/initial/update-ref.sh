basic()
{
  update "-p $PWD" basic
  move_cpp BasicStateMachine
}

junction()
{
  update "-p $PWD" junction
  move_cpp JunctionStateMachine
}

nested()
{
  update "-p $PWD" nested
  move_cpp NestedStateMachine
}
