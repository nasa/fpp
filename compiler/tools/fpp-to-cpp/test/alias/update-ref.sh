move_h_hpp()
{
  if test $# -ne 1
  then
    echo 'usage: move_h_hpp file' 1>&2
    exit 1
  fi
  file=$1
  for suffix in hpp h
  do
    mv $file'Ac.'$suffix $file'Ac.ref.'$suffix
  done
}

move_hpp()
{
  if test $# -ne 1
  then
    echo 'usage: move_hpp file' 1>&2
    exit 1
  fi
  file=$1
  mv $file'Ac.hpp' $file'Ac.ref.hpp'
}

abs_type()
{
  update "-p $PWD" abs_type
  move_hpp AbsTypeAlias
}

basic()
{
  update "-p $PWD" basic
  move_h_hpp TF32Alias
  move_h_hpp TU32Alias
  move_hpp TStringAlias
  move_hpp TStringSizeAlias
}

builtin_type()
{
  update "-p $PWD" builtin_type
  move_h_hpp BuiltInTypeAlias
  move_hpp NamespacedBuiltin1Alias
  move_hpp NamespacedBuiltin2Alias
}

namespace()
{
  update "-p $PWD" namespace
  move_h_hpp SimpleCTypeAlias
  move_h_hpp SimpleCType2Alias
  move_hpp NamespacedAliasTypeAlias
  move_hpp NamespacedAliasType2Alias
}

component()
{
  update "-p $PWD" component
  move_cpp C_AArray
  move_cpp CComponent
  move_h_hpp TAlias
  move_hpp C_TAlias
}
