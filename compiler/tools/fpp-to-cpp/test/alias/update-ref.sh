abs_type()
{
  update "-p $PWD" abs_type
  move_hpp AbsTypeAlias
  move_cpp AbsSerializable
}

basic()
{
  update "-p $PWD" basic
  move_h_hpp TF32Alias
  move_h_hpp TU32Alias
  move_hpp TStringAlias
  move_hpp TStringSizeAlias
  move_cpp BasicSerializable
}

builtin_type()
{
  update "-p $PWD" builtin_type
  move_h_hpp BuiltInTypeAlias
  move_hpp NamespacedBuiltin1Alias
  move_hpp NamespacedBuiltin2Alias
  move_cpp BuiltinSerializable
}

namespace()
{
  update "-p $PWD" namespace
  move_h_hpp SimpleCTypeAlias
  move_h_hpp SimpleCType2Alias
  move_hpp NamespacedAliasTypeAlias
  move_hpp NamespacedAliasType2Alias
  move_cpp NamespaceSerializable
}
