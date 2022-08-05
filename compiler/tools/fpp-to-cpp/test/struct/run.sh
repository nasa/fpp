primitive()
{
  run_test "-p $PWD" primitive && \
    diff_cpp PrimitiveSerializable && \
    diff_cpp PrimitiveStructSerializable
}

string()
{
  run_test "-p $PWD" string && \
    diff_cpp StringSerializable && \
    diff_cpp StringArraySerializable
}

enum()
{
  run_test "-p $PWD" enum && \
    diff_cpp EEnum && \
    diff_cpp EnumSerializable 
}

abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_cpp AbsTypeSerializable
}

empty()
{
  run_test "-p $PWD" empty && \
    diff_cpp EmptySerializable
}

default()
{
  run_test "-p $PWD" default && \
    diff_cpp DefaultSerializable
}

format()
{
  run_test "-p $PWD" format && \
    diff_cpp FormatSerializable
}

modules()
{
  run_test "-p $PWD" modules && \
    diff_cpp Modules1Serializable && \
    diff_cpp Modules2Serializable && \
    diff_cpp Modules3Serializable
}

component()
{
  run_test "-p $PWD" component && \
    diff_cpp C_SSerializable && \
    diff_cpp SSerializable
}
