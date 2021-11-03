#!/bin/sh

array_enum()
{
  update "-p $PWD" array_enum
  move_xml ArrayEnumE1Enum ArrayEnumE2Enum ArrayEnum1Array ArrayEnum2Array
}

array_ok()
{
  update "-p $PWD" array_ok
  indices=`seq 1 5`
  move_xml ArrayOK1Array ArrayOK2Array ArrayOK3Array ArrayOK4Array ArrayOK5Array
}

array_struct()
{
  update "-p $PWD" array_struct
  move_xml ArrayStructS1Serializable ArrayStructS2Serializable ArrayStruct1Array ArrayStruct2Array
}


array_struct_member_array()
{
  update "-p $PWD" array_struct_member_array
  move_xml ArrayStructMemberArraySerializable ArrayStructMemberArrayArray
}
built_in_type()
{
  update "-p $PWD" built_in_type
  move_xml BuiltInTypeArray
}
