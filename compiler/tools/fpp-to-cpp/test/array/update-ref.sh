primitive()
{
  update "-p $PWD" primitive
  move_cpp Primitive1Array 
  move_cpp Primitive2Array 
  move_cpp PrimitiveArrayArray
}

string()
{
  update "-p $PWD" string
  move_cpp String1Array 
  move_cpp String2Array 
  move_cpp StringArrayArray
}

enum()
{
  update "-p $PWD" enum
  move_cpp E1Enum 
  move_cpp E2Enum 
  move_cpp Enum1Array 
  move_cpp Enum2Array
}

builtin_type()
{
  update "-p $PWD" builtin_type
  move_cpp BuiltInTypeArray
}

abs_type()
{
  update "-p $PWD" abs_type
  move_cpp AbsTypeArray
}
