. ./fpp-options.sh

abs_type()
{
  update "$fpp_options" abs_type
  move_hpp AbsTypeAlias
  move_cpp AbsSerializable
}

basic()
{
  update "$fpp_options" basic
  move_h_hpp TF32Alias
  move_h_hpp TU32Alias
  move_hpp TStringAlias
  move_hpp TStringSizeAlias
  move_cpp BasicSerializable
}

component()
{
  update "$fpp_options" component
  move_hpp C_XAlias && \
  move_cpp C_AArray
}
namespace()
{
  update "$fpp_options" namespace
  move_h_hpp SimpleCTypeAlias
  move_h_hpp SimpleCType2Alias
  move_hpp NamespacedAliasTypeAlias
  move_hpp NamespacedAliasType2Alias
  move_cpp NamespaceSerializable
}

state_machine()
{
  update "$fpp_options" state_machine
  move_hpp SM_XAlias && \
  move_cpp SM_AArray
}
