. ./fpp-options.sh

abs_type()
{
  run_test "$fpp_options" abs_type && \
    diff_hpp AbsTypeAlias && \
    diff_cpp AbsSerializable
}

basic()
{
  run_test "$fpp_options" basic && \
    diff_h_hpp TU32Alias && \
    diff_h_hpp TF32Alias && \
    diff_hpp TStringAlias && \
    diff_hpp TStringSizeAlias && \
    diff_cpp BasicSerializable
}

component()
{
  run_test "$fpp_options" component && \
    diff_hpp C_XAlias && \
    diff_cpp C_AArray
}

namespace()
{
  run_test "$fpp_options" namespace && \
    diff_h_hpp SimpleCTypeAlias && \
    diff_h_hpp SimpleCType2Alias && \
    diff_hpp NamespacedAliasTypeAlias && \
    diff_hpp NamespacedAliasType2Alias && \
    diff_cpp NamespaceSerializable
}

state_machine()
{
  run_test "$fpp_options" state_machine && \
    diff_hpp SM_XAlias && \
    diff_cpp SM_AArray
}
