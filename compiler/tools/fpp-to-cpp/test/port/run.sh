abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_cpp AbsTypePort
}

builtin_type()
{
  run_test "-p $PWD" builtin_type && \
    diff_cpp BuiltInTypePort
}

empty()
{
  run_test "-p $PWD" empty && \
    diff_cpp EmptyPort
}

fpp_type()
{
  run_test "-p $PWD" fpp_type && \
    diff_cpp FppTypePort
}

primitive()
{
  run_test "-p $PWD" primitive && \
    diff_cpp PrimitivePort
}

return_type()
{
  run_test "-p $PWD" return_type && \
    diff_cpp ReturnTypePort
}

string()
{
  run_test "-p $PWD" string && \
    diff_cpp StringPort
}
