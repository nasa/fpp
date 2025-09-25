abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_cpp AbsTypeArray
}

alias_type()
{
  run_test "-p $PWD" alias_type && \
    diff_cpp AliasTypeArray && \
    diff -u ATAliasAc.ref.h ATAliasAc.h && \
    diff -u ATAliasAc.ref.hpp ATAliasAc.hpp
}

component()
{
  run_test "-p $PWD" component && \
    diff_cpp C_AArray && \
    diff_cpp AArray
}

enum()
{
  run_test "-p $PWD" enum && \
    diff_cpp E1Enum && \
    diff_cpp E2Enum && \
    diff_cpp Enum1Array && \
    diff_cpp Enum2Array
}

header_path()
{
  run_test "-p $PWD" "include/T.fpp header_path" header_path && \
    diff_cpp HeaderPathArray
}

large_size()
{
  run_test "-p $PWD" large_size && \
    diff_cpp LargeSizeArray
}

primitive()
{
  run_test "-p $PWD" primitive && \
    diff_cpp PrimitiveBoolArray && \
    diff_cpp PrimitiveU8Array && \
    diff_cpp PrimitiveU16Array && \
    diff_cpp PrimitiveI32Array && \
    diff_cpp PrimitiveI64Array && \
    diff_cpp PrimitiveF32fArray && \
    diff_cpp PrimitiveF32eArray && \
    diff_cpp PrimitiveF64Array && \
    diff_cpp PrimitiveArrayArray
}

single_element()
{
  run_test "-p $PWD" single_element && \
    diff_cpp SingleElementArray
}

string()
{
  run_test "-p $PWD" string && \
    diff_cpp String1Array && \
    diff_cpp String2Array && \
    diff_cpp StringArrayArray
}

struct()
{
  run_test "-p $PWD" struct && \
    diff_cpp Struct1Array && \
    diff_cpp Struct2Array && \
    diff_cpp Struct3Array && \
    diff_cpp S1Serializable && \
    diff_cpp S2Serializable && \
    diff_cpp S3Serializable
}

