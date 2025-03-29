abs_type()
{
  run_test "-p $PWD" "include/T.fpp abs_type" abs_type && \
    diff_cpp AbsTypePort
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

kwd_name()
{
  run_test "-p $PWD" kwd_name && \
    diff_cpp KwdNamePort
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

string_return_type()
{
  run_test "-p $PWD" string_return_type && \
    diff_cpp StringReturnTypePort
}
