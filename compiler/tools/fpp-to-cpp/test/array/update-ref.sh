primitive()
{
  update "-p $PWD" primitive
  move_cpp Primitive1Array Primitive2Array PrimitiveArrayArray
}

string()
{
  update "-p $PWD" string
  move_cpp String1Array String2Array StringArrayArray
}

enum()
{
  update "-p $PWD" enum
  move_cpp E1Enum E2Enum Enum1Array Enum2Array
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
