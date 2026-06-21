. ./fpp-options.sh

abs_type()
{
  update "$fpp_options" abs_type
  move_cpp AbsTypeSerializable
}

alias_type()
{
  update "$fpp_options" alias_type
  move_cpp AliasTypeSerializable
  move_h_hpp U16AliasAlias
  move_hpp TAliasAlias
  move_hpp SAliasAlias
}

array()
{
  update "$fpp_options" array
  move_cpp AArray
}

component()
{
  update "$fpp_options" component
  move_cpp C_SSerializable
  move_cpp SSerializable
}

default()
{
  update "$fpp_options" default
  move_cpp DefaultSerializable
}

empty()
{
  update "$fpp_options" empty
  move_cpp EmptySerializable
}

enum()
{
  update "$fpp_options" enum
  move_cpp EEnum
  move_cpp EnumSerializable 
}

format()
{
  update "$fpp_options" format
  move_cpp FormatSerializable
}

include()
{
  update "$fpp_options" include
  move_cpp IncludedSerializable
  move_cpp IncludingSerializable
}

modules()
{
  update "$fpp_options" modules
  move_cpp Modules1Serializable
  move_cpp Modules2Serializable
  move_cpp Modules3Serializable
  move_cpp Modules4Serializable
}

primitive()
{
  update "$fpp_options" primitive
  move_cpp PrimitiveSerializable
  move_cpp PrimitiveStructSerializable
}

state_machine()
{
  update "$fpp_options" state_machine
  move_cpp SM_SSerializable
}

string()
{
  update "$fpp_options" string
  move_cpp StringSerializable
  move_cpp StringArraySerializable
}

