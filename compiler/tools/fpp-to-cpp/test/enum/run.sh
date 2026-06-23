alias_serial_type()
{
  run_test "-p $PWD" alias_serial_type && \
    diff_cpp AliasSerialTypeEnum
}

component()
{
  run_test "-p $PWD" component && \
    diff_cpp C_EEnum && \
    diff_cpp EEnum
}

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

singleton_range()
{
  run_test "-p $PWD" singleton_range && \
    diff_cpp SingletonRangeEnum
}

serialize_type()
{
  run_test "-p $PWD" serialize_type && \
    diff_cpp SerializeTypeEnum
}

state_machine()
{
  run_test "-p $PWD" state_machine && \
    diff_cpp SM_EEnum
}
