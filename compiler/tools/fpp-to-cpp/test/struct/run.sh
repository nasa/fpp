abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_cpp AbsTypeSerializable
}

alias_type()
{
  run_test "-p $PWD" alias_type && \
    diff_cpp AliasTypeSerializable && \
    diff_h_hpp U16AliasAlias && \
    diff_hpp TAliasAlias && \
    diff_hpp SAliasAlias
}

array()
{
  run_test "-p $PWD" array && \
    diff_cpp AArray
}

component()
{
  run_test "-p $PWD" component && \
    diff_cpp C_SSerializable && \
    diff_cpp SSerializable
}

default()
{
  run_test "-p $PWD" default && \
    diff_cpp DefaultSerializable
}

empty()
{
  run_test "-p $PWD" empty && \
    diff_cpp EmptySerializable
}

enum()
{
  run_test "-p $PWD" enum && \
    diff_cpp EEnum && \
    diff_cpp EnumSerializable 
}

format()
{
  run_test "-p $PWD" format && \
    diff_cpp FormatSerializable
}

include()
{
  run_test "-p $PWD" include && \
    diff_cpp IncludedSerializable && \
    diff_cpp IncludingSerializable
}

modules()
{
  run_test "-p $PWD" modules && \
    diff_cpp Modules1Serializable && \
    diff_cpp Modules2Serializable && \
    diff_cpp Modules3Serializable && \
    diff_cpp Modules4Serializable
}

primitive()
{
  run_test "-p $PWD" primitive && \
    diff_cpp PrimitiveSerializable && \
    diff_cpp PrimitiveStructSerializable
}

state_machine()
{
  run_test "-p $PWD" state_machine && \
    diff_cpp SM_SSerializable
}

string()
{
  run_test "-p $PWD" string && \
    diff_cpp StringSerializable && \
    diff_cpp StringArraySerializable
}

