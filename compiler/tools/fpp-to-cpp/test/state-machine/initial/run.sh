basic()
{
  run_test "-p $PWD" basic && \
    diff_cpp BasicStateMachine
}
