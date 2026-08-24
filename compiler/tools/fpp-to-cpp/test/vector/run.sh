. ./fpp-options.sh

abs_type()
{
  run_test "$fpp_options" abs_type && \
    diff_cpp AbsTypeVector
}

component()
{
  run_test "$fpp_options" component && \
    diff_cpp C_AVector && \
    diff_cpp AVector
}

default_value()
{
  run_test "$fpp_options" default_value && \
    diff_cpp DefaultValueVector
}

duplicate()
{
  run_test "$fpp_options" duplicate
}

enum()
{
  run_test "$fpp_options" enum && \
    diff_cpp E1Enum && \
    diff_cpp Enum1Vector && \
    diff_cpp Enum2Vector
}

nested()
{
  run_test "$fpp_options" nested && \
    diff_cpp InnerVector && \
    diff_cpp OuterVector && \
    diff_cpp ArrayOfVectorArray
}

single_element()
{
  run_test "$fpp_options" single_element && \
    diff_cpp SingleElementVector
}

size_prefix()
{
  run_test "$fpp_options" size_prefix && \
    diff_cpp ExplicitPrefixVector && \
    diff_cpp AliasPrefixVector && \
    diff_h_hpp MyU8Alias
}

string()
{
  run_test "$fpp_options" string && \
    diff_cpp String1Vector && \
    diff_cpp String2Vector && \
    diff_cpp StringVectorVector
}

struct()
{
  run_test "$fpp_options" struct && \
    diff_cpp Struct1Vector && \
    diff_cpp S1Serializable
}
