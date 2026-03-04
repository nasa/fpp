. ./fpp-options.sh

abs_type()
{
  update "$fpp_options" "include/T.fpp abs_type" abs_type
  move_cpp AbsTypePort
}

empty()
{
  update "$fpp_options" empty
  move_cpp EmptyPort
}

fpp_type()
{
  update "$fpp_options" fpp_type
  move_cpp FppTypePort
}

kwd_name()
{
  update "$fpp_options" kwd_name
  move_cpp KwdNamePort
}

primitive()
{
  update "$fpp_options" primitive
  move_cpp PrimitivePort
}

return_type()
{
  update "$fpp_options" return_type
  move_cpp ReturnTypePort
}

string()
{
  update "$fpp_options" string
  move_cpp StringPort
}

string_return_type()
{
  update "$fpp_options" string_return_type
  move_cpp StringReturnTypePort
}
