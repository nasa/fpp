abs_type()
{
  update "-p $PWD" "include/T.fpp abs_type" abs_type
  move_cpp AbsTypePort
}

builtin_type()
{
  update "-p $PWD" builtin_type
  move_cpp BuiltInTypePort
}

empty()
{
  update "-p $PWD" empty
  move_cpp EmptyPort
}

fpp_type()
{
  update "-p $PWD" fpp_type
  move_cpp FppTypePort
}

kwd_name()
{
  update "-p $PWD" kwd_name
  move_cpp KwdNamePort
}

primitive()
{
  update "-p $PWD" primitive
  move_cpp PrimitivePort
}

return_type()
{
  update "-p $PWD" return_type
  move_cpp ReturnTypePort
}

string()
{
  update "-p $PWD" string
  move_cpp StringPort
}
