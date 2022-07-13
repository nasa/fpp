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
}

enum()
{
  update "-p $PWD" enum
  move_cpp EEnum
  move_cpp EnumSerializable 
}

array()
{
  update "-p $PWD" array
  move_cpp StringArraySerializable
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

duplicate()
{
  update "-p $PWD" duplicate
  move_cpp DuplicateSerializable
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
