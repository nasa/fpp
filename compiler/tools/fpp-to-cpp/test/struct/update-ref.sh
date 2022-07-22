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

modules()
{
  update "-p $PWD" modules
  move_cpp Modules1Serializable
  move_cpp Modules2Serializable
  move_cpp Modules3Serializable
}

component()
{
  update "-p $PWD" component
  move_cpp C_SSerializable
  move_cpp SSerializable
}
