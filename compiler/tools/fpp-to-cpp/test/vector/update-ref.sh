. ./fpp-options.sh

abs_type()
{
  update "$fpp_options" abs_type
  move_cpp AbsTypeVector
}

component()
{
  update "$fpp_options" component
  move_cpp C_AVector
  move_cpp AVector
}

default_value()
{
  update "$fpp_options" default_value
  move_cpp DefaultValueVector
}

duplicate()
{
  update "$fpp_options" duplicate
}

enum()
{
  update "$fpp_options" enum
  move_cpp E1Enum
  move_cpp Enum1Vector
  move_cpp Enum2Vector
}

nested()
{
  update "$fpp_options" nested
  move_cpp InnerVector
  move_cpp OuterVector
  move_cpp ArrayOfVectorArray
}

single_element()
{
  update "$fpp_options" single_element
  move_cpp SingleElementVector
}

size_prefix()
{
  update "$fpp_options" size_prefix
  move_cpp ExplicitPrefixVector
  move_cpp AliasPrefixVector
  move_h_hpp MyU8Alias
}

string()
{
  update "$fpp_options" string
  move_cpp String1Vector
  move_cpp String2Vector
  move_cpp StringVectorVector
}

struct()
{
  update "$fpp_options" struct
  move_cpp Struct1Vector
  move_cpp S1Serializable
}
