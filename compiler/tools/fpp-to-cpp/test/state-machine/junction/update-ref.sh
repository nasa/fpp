. ./fpp-flags.sh

basic()
{
  update "$fpp_flags" Basic
  move_cpp BasicStateMachine
}

basic_u32()
{
  update "$fpp_flags" BasicU32
  move_cpp BasicU32StateMachine
}

sequence()
{
  update "$fpp_flags" Sequence
  move_cpp SequenceStateMachine
}

