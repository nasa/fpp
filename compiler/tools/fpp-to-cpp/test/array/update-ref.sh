primitive()
{
  update "-p $PWD" primitive
  move_cpp PrimitiveBoolArray
  move_cpp PrimitiveU8Array
  move_cpp PrimitiveU16Array
  move_cpp PrimitiveI32Array
  move_cpp PrimitiveI64Array
  move_cpp PrimitiveF32fArray
  move_cpp PrimitiveF32eArray
  move_cpp PrimitiveF64Array
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

struct()
{
  update "-p $PWD" struct
  move_cpp Struct1Array
  move_cpp Struct2Array
  move_cpp Struct3Array
  move_cpp S1Serializable
  move_cpp S2Serializable
  move_cpp S3Serializable
}

component()
{
  update "-p $PWD" component
  move_cpp C_AArray
  move_cpp AArray
}

header_path()
{
  update "-p $PWD" "include/T.fpp header_path" header_path
  move_cpp HeaderPathArray
}

single_element()
{
  update "-p $PWD" single_element
  move_cpp SingleElementArray
}
