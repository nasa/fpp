. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" basic && \
    diff_cpp BasicStateMachine
}
