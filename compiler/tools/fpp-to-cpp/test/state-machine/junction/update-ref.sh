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

input_pair_u16_u32()
{
  update "$fpp_flags" InputPairU16U32
  move_cpp InputPairU16U32StateMachine
}

junction_to_junction()
{
  update "$fpp_flags" JunctionToJunction
  move_cpp JunctionToJunctionStateMachine
}

junction_to_state()
{
  update "$fpp_flags" JunctionToState
  move_cpp JunctionToStateStateMachine
}

sequence()
{
  update "$fpp_flags" Sequence
  move_cpp SequenceStateMachine
}

sequence_u32()
{
  update "$fpp_flags" SequenceU32
  move_cpp SequenceU32StateMachine
}
