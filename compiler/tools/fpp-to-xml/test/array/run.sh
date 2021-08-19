#!/bin/sh

array_enum()
{
  run_test "-p $PWD" array_enum && \
    diff_xml ArrayEnumE1Enum ArrayEnumE2Enum ArrayEnum1Array ArrayEnum2Array
}

array_ok()
{
  run_test "-p $PWD" array_ok && \
    diff_xml ArrayOK1Array ArrayOK2Array ArrayOK3Array ArrayOK4Array ArrayOK5Array
}

array_struct()
{
  run_test "-p $PWD" array_struct && \
    diff_xml ArrayStructS1Serializable ArrayStructS2Serializable ArrayStruct1Array ArrayStruct2Array
}

built_in_type()
{
  run_test "-p $PWD" built_in_type && \
    diff_xml BuiltInTypeArray
}

array_struct_with_array()
{
  run_test "-p $PWD" array_struct_with_array && \
    diff_xml ArrayStructS3Serializable ArrayStruct3Array
}