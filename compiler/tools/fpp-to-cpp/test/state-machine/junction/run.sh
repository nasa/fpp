. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" Basic && \
    diff_cpp BasicStateMachine
}

basic_u32()
{
  run_test "$fpp_flags" BasicU32 && \
    diff_cpp BasicU32StateMachine
}

sequence()
{
  run_test "$fpp_flags" Sequence && \
    diff_cpp SequenceStateMachine
}

sequence_u32()
{
  run_test "$fpp_flags" SequenceU32 && \
    diff_cpp SequenceU32StateMachine
}
