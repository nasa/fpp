. ./fpp-options.sh

abs_type()
{
  run_test "$fpp_options" "include/T.fpp abs_type" abs_type && \
    diff_cpp AbsTypePort
}

empty()
{
  run_test "$fpp_options" empty && \
    diff_cpp EmptyPort
}

fpp_type()
{
  run_test "$fpp_options" fpp_type && \
    diff_cpp FppTypePort
}

kwd_name()
{
  run_test "$fpp_options" kwd_name && \
    diff_cpp KwdNamePort
}

primitive()
{
  run_test "$fpp_options" primitive && \
    diff_cpp PrimitivePort
}

return_type()
{
  run_test "$fpp_options" return_type && \
    diff_cpp ReturnTypePort
}

string()
{
  run_test "$fpp_options" string && \
    diff_cpp StringPort
}

string_return_type()
{
  run_test "$fpp_options" string_return_type && \
    diff_cpp StringReturnTypePort
}
