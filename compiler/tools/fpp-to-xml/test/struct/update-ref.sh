#!/bin/sh

enum()
{
  update '' enum
  mv EEnumAi.xml EEnumAi.ref.xml
}

struct_abs_type()
{
  update "-p $PWD" struct_abs_type
  mv StructAbsTypeSerializableAi.xml StructAbsTypeSerializableAi.ref.xml
}

struct_default()
{
  update "-p $PWD" struct_default
  mv StructDefaultSerializableAi.xml StructDefaultSerializableAi.ref.xml
}

struct_enum_member()
{
  update "-i enum.fpp -p $PWD" struct_enum_member
  mv StructEnumMemberSerializableAi.xml StructEnumMemberSerializableAi.ref.xml
}

struct_ok()
{
  update "-n struct_ok.names.txt -p $PWD" struct_ok
  mv struct_ok.names.txt struct_ok.names.ref.txt
  mv StructOK1SerializableAi.xml StructOK1SerializableAi.ref.xml
  mv StructOK2SerializableAi.xml StructOK2SerializableAi.ref.xml
}

struct_format()
{
  update '' struct_format
  mv StructFormatSerializableAi.xml StructFormatSerializableAi.ref.xml
}

struct_modules()
{
  update "-p $PWD" struct_modules
  mv StructModules1SerializableAi.xml StructModules1SerializableAi.ref.xml
  mv StructModules2SerializableAi.xml StructModules2SerializableAi.ref.xml
  mv StructModules3SerializableAi.xml StructModules3SerializableAi.ref.xml
}

struct_output_dir()
{
  update '-d output_dir' output_dir/struct_output_dir
  mv output_dir/StructOutputDirSerializableAi.xml output_dir/StructOutputDirSerializableAi.ref.xml
}

struct_string()
{
  update '' struct_string
  mv StructStringSerializableAi.xml StructStringSerializableAi.ref.xml
}

struct_string_array()
{
  update '' struct_string_array
  mv StructStringArraySerializableAi.xml StructStringArraySerializableAi.ref.xml 
}
