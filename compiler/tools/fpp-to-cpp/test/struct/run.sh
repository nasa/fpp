. ./fpp-options.sh

abs_type()
{
  run_test "$fpp_options" abs_type && \
    diff_cpp AbsTypeSerializable
}

alias_type()
{
  run_test "$fpp_options" alias_type && \
    diff_cpp AliasTypeSerializable && \
    diff_h_hpp U16AliasAlias && \
    diff_hpp TAliasAlias && \
    diff_hpp SAliasAlias
}

array()
{
  run_test "$fpp_options" array && \
    diff_cpp AArray
}

component()
{
  run_test "$fpp_options" component && \
    diff_cpp C_SSerializable && \
    diff_cpp SSerializable
}

default()
{
  run_test "$fpp_options" default && \
    diff_cpp DefaultSerializable
}

empty()
{
  run_test "$fpp_options" empty && \
    diff_cpp EmptySerializable
}

enum()
{
  run_test "$fpp_options" enum && \
    diff_cpp EEnum && \
    diff_cpp EnumSerializable 
}

format()
{
  run_test "$fpp_options" format && \
    diff_cpp FormatSerializable
}

include()
{
  run_test "$fpp_options" include && \
    diff_cpp IncludedSerializable && \
    diff_cpp IncludingSerializable
}

modules()
{
  run_test "$fpp_options" modules && \
    diff_cpp Modules1Serializable && \
    diff_cpp Modules2Serializable && \
    diff_cpp Modules3Serializable && \
    diff_cpp Modules4Serializable
}

primitive()
{
  run_test "$fpp_options" primitive && \
    diff_cpp PrimitiveSerializable && \
    diff_cpp PrimitiveStructSerializable
}

state_machine()
{
  run_test "$fpp_options" state_machine && \
    diff_cpp SM_SSerializable
}

string()
{
  run_test "$fpp_options" string && \
    diff_cpp StringSerializable && \
    diff_cpp StringArraySerializable
}

