primitive()
{
  run_test "-p $PWD" primitive && \
    diff_cpp Primitive1Array 
    diff_cpp Primitive2Array 
    diff_cpp PrimitiveArrayArray
}

string()
{
  run_test "-p $PWD" string && \
    diff_cpp String1Array 
    diff_cpp String2Array 
    diff_cpp StringArrayArray
}

enum()
{
  run_test "-p $PWD" enum && \
    diff_cpp E1Enum 
    diff_cpp E2Enum 
    diff_cpp Enum1Array 
    diff_cpp Enum2Array
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
