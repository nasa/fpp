. ./fpp-flags.sh

basic()
{
  update "$fpp_flags" basic
  move_cpp BasicStateMachine
}

basic_u32()
{
  update "$fpp_flags" basic_u32
  move_cpp BasicU32StateMachine
}
