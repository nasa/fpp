abs_type()
{
  run_test "-p $PWD" abs_type && \
    diff_hpp AbsTypeAlias
}

basic()
{
  run_test "-p $PWD" basic && \
    diff_h_hpp TU32Alias && \
    diff_h_hpp TF32Alias && \
    diff_hpp TStringAlias && \
    diff_hpp TStringSizeAlias
}

builtin_type()
{
  run_test "-p $PWD" builtin_type && \
    diff_h_hpp BuiltInTypeAlias && \
    diff_hpp NamespacedBuiltin1Alias && \
    diff_hpp NamespacedBuiltin2Alias
}

namespace()
{
  run_test "-p $PWD" namespace && \
    diff_h_hpp SimpleCTypeAlias && \
    diff_h_hpp SimpleCType2Alias && \
    diff_hpp NamespacedAliasTypeAlias && \
    diff_hpp NamespacedAliasType2Alias
}
