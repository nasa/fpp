. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" basic && \
    diff_cpp BasicStateMachine
}

basic_u32()
{
  run_test "$fpp_flags" basic_u32 && \
    diff_cpp BasicU32StateMachine
}
