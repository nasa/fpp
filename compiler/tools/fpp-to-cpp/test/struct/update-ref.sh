primitive()
{
  update "-p $PWD" primitive
  move_cpp PrimitiveSerializable
  move_cpp PrimitiveStructSerializable
}

string()
{
  update "-p $PWD" string
  move_cpp StringSerializable
  move_cpp StringArraySerializable
}

enum()
{
  update "-p $PWD" enum
  move_cpp EEnum
  move_cpp EnumSerializable 
}

abs_type()
{
  update "-p $PWD" abs_type
  move_cpp AbsTypeSerializable
}

alias_type()
{
  update "-p $PWD" alias_type
  move_cpp AliasTypeSerializable
  mv U16AliasAliasAc.h U16AliasAliasAc.ref.h
  mv U16AliasAliasAc.hpp U16AliasAliasAc.ref.hpp
  mv TAliasAliasAc.hpp TAliasAliasAc.ref.hpp
}

empty()
{
  update "-p $PWD" empty
  move_cpp EmptySerializable
}

default()
{
  update "-p $PWD" default
  move_cpp DefaultSerializable
}

format()
{
  update "-p $PWD" format
  move_cpp FormatSerializable
}

include()
{
  update "-p $PWD" include
  move_cpp IncludedSerializable
  move_cpp IncludingSerializable
}

modules()
{
  update "-p $PWD" modules
  move_cpp Modules1Serializable
  move_cpp Modules2Serializable
  move_cpp Modules3Serializable
  move_cpp Modules4Serializable
}

component()
{
  update "-p $PWD" component
  move_cpp C_SSerializable
  move_cpp SSerializable
}
