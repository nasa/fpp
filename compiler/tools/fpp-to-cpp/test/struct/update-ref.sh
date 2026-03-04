abs_type()
{
  update "-p $PWD" abs_type
  move_cpp AbsTypeSerializable
}

alias_type()
{
  update "-p $PWD" alias_type
  move_cpp AliasTypeSerializable
  move_h_hpp U16AliasAlias
  move_hpp TAliasAlias
  move_hpp SAliasAlias
}

array()
{
  update "-p $PWD" array
  move_cpp AArray
}

component()
{
  update "-p $PWD" component
  move_cpp C_SSerializable
  move_cpp SSerializable
}

default()
{
  update "-p $PWD" default
  move_cpp DefaultSerializable
}

empty()
{
  update "-p $PWD" empty
  move_cpp EmptySerializable
}

enum()
{
  update "-p $PWD" enum
  move_cpp EEnum
  move_cpp EnumSerializable 
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

primitive()
{
  update "-p $PWD" primitive
  move_cpp PrimitiveSerializable
  move_cpp PrimitiveStructSerializable
}

state_machine()
{
  update "-p $PWD" state_machine
  move_cpp SM_SSerializable
}

string()
{
  update "-p $PWD" string
  move_cpp StringSerializable
  move_cpp StringArraySerializable
}

