primitive()
{
  run_test "-p $PWD" primitive && \
    diff_cpp Primitive1Array Primitive2Array PrimitiveArrayArray
}

string()
{
  run_test "-p $PWD" string && \
    diff_cpp String1Array String2Array StringArrayArray
}

enum()
{
  run_test "-p $PWD" enum && \
    diff_cpp E1Enum E2Enum Enum1Array Enum2Array
}

builtin_type()
{
  run_test "-p $PWD" builtin_type && \
    diff_cpp BuiltInTypeArray
}

abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_cpp AbsTypeArray
}
