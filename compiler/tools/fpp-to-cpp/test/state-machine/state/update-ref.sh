basic()
{
  update "-p $PWD" basic
  move_cpp BasicStateMachine
}

basic_guard()
{
  update "-p $PWD" basic_guard
  move_cpp BasicGuardStateMachine
}

basic_self()
{
  update "-p $PWD" basic_self
  move_cpp BasicSelfStateMachine
}
