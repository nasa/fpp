. ./fpp-options.sh

abs_type()
{
  run_test "$fpp_options" abs_type && \
    diff_cpp AbsTypeArray
}

alias_type()
{
  run_test "$fpp_options" alias_type && \
    diff_cpp AliasTypeArray && \
    diff -u ATAliasAc.ref.h ATAliasAc.h && \
    diff -u ATAliasAc.ref.hpp ATAliasAc.hpp
}

component()
{
  run_test "$fpp_options" component && \
    diff_cpp C_AArray && \
    diff_cpp AArray
}

enum()
{
  run_test "$fpp_options" enum && \
    diff_cpp E1Enum && \
    diff_cpp E2Enum && \
    diff_cpp Enum1Array && \
    diff_cpp Enum2Array
}

header_path()
{
  run_test "$fpp_options" "include/T.fpp header_path" header_path && \
    diff_cpp HeaderPathArray
}

large_size()
{
  run_test "$fpp_options" large_size && \
    diff_cpp LargeSizeArray
}

primitive()
{
  run_test "$fpp_options" primitive && \
    diff_cpp PrimitiveBoolArray && \
    diff_cpp PrimitiveU8Array && \
    diff_cpp PrimitiveU16Array && \
    diff_cpp PrimitiveI32Array && \
    diff_cpp PrimitiveI64Array && \
    diff_cpp PrimitiveF32fArray && \
    diff_cpp PrimitiveF32eArray && \
    diff_cpp PrimitiveF64Array && \
    diff_cpp PrimitiveArrayArray && \
    diff_cpp BitMaskArrayArray
}

single_element()
{
  run_test "$fpp_options" single_element && \
    diff_cpp SingleElementArray
}

state_machine()
{
  run_test "$fpp_options" state_machine && \
    diff_cpp SM_AArray
}

string()
{
  run_test "$fpp_options" string && \
    diff_cpp String1Array && \
    diff_cpp String2Array && \
    diff_cpp StringArrayArray
}

struct()
{
  run_test "$fpp_options" struct && \
    diff_cpp Struct1Array && \
    diff_cpp Struct2Array && \
    diff_cpp Struct3Array && \
    diff_cpp Struct4Array && \
    diff_cpp S1Serializable && \
    diff_cpp S2Serializable && \
    diff_cpp S3Serializable && \
    diff_cpp SWrapperSerializable && \
    diff_cpp SDefaultSerializable
}

