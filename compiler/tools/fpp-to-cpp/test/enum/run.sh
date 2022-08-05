default()
{
  run_test "-p $PWD" default && \
    diff_cpp DefaultEnum
}

explicit()
{
  run_test "-p $PWD" explicit && \
    diff_cpp ExplicitEnum
}

implicit()
{
  run_test "-p $PWD" implicit && \
    diff_cpp ImplicitEnum
}

serialize_type()
{
  run_test "-p $PWD" serialize_type && \
    diff_cpp SerializeTypeEnum
}

component()
{
  run_test "-p $PWD" component && \
    diff_cpp C_EEnum && \
    diff_cpp EEnum
}
