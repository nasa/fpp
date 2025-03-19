abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_hpp AbsTypeAlias && \
    diff_cpp AbsSerializable
}

basic()
{
  run_test "-p $PWD" basic && \
    diff_h_hpp TU32Alias && \
    diff_h_hpp TF32Alias && \
    diff_hpp TStringAlias && \
    diff_hpp TStringSizeAlias && \
    diff_cpp BasicSerializable
}

builtin_type()
{
  run_test "-p $PWD" builtin_type && \
    diff_h_hpp BuiltInTypeAlias && \
    diff_hpp NamespacedBuiltin1Alias && \
    diff_hpp NamespacedBuiltin2Alias && \
    diff_cpp BuiltinSerializable
}

namespace()
{
  run_test "-p $PWD" namespace && \
    diff_h_hpp SimpleCTypeAlias && \
    diff_h_hpp SimpleCType2Alias && \
    diff_hpp NamespacedAliasTypeAlias && \
    diff_hpp NamespacedAliasType2Alias && \
    diff_cpp NamespaceSerializable
}
