. ./fpp-options.sh

abs_type()
{
  update "$fpp_options" abs_type
  move_cpp AbsTypeArray
}

alias_type()
{
  update "$fpp_options" alias_type
  move_cpp AliasTypeArray
  mv ATAliasAc.hpp ATAliasAc.ref.hpp
  mv ATAliasAc.h ATAliasAc.ref.h
}

component()
{
  update "$fpp_options" component
  move_cpp C_AArray
  move_cpp AArray
}

enum()
{
  update "$fpp_options" enum
  move_cpp E1Enum
  move_cpp E2Enum
  move_cpp Enum1Array
  move_cpp Enum2Array
}

header_path()
{
  update "$fpp_options" "include/T.fpp header_path" header_path
  move_cpp HeaderPathArray
}

large_size()
{
  update "$fpp_options" large_size
  move_cpp LargeSizeArray
}

primitive()
{
  update "$fpp_options" primitive
  move_cpp PrimitiveBoolArray
  move_cpp PrimitiveU8Array
  move_cpp PrimitiveU16Array
  move_cpp PrimitiveI32Array
  move_cpp PrimitiveI64Array
  move_cpp PrimitiveF32fArray
  move_cpp PrimitiveF32eArray
  move_cpp PrimitiveF64Array
  move_cpp PrimitiveArrayArray
  move_cpp BitMaskArrayArray
}

single_element()
{
  update "$fpp_options" single_element
  move_cpp SingleElementArray
}

state_machine()
{
  update "$fpp_options" state_machine
  move_cpp SM_AArray
}

string()
{
  update "$fpp_options" string
  move_cpp String1Array
  move_cpp String2Array
  move_cpp StringArrayArray
}

struct()
{
  update "$fpp_options" struct
  move_cpp Struct1Array
  move_cpp Struct2Array
  move_cpp Struct3Array
  move_cpp Struct4Array
  move_cpp S1Serializable
  move_cpp S2Serializable
  move_cpp S3Serializable
  move_cpp SWrapperSerializable
  move_cpp SDefaultSerializable
}

