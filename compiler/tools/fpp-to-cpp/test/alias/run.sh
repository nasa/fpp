diff_h_hpp()
{
  if test $# -ne 1
  then
    echo 'usage: diff_h_hpp file' 1>&2
    exit 1
  fi
  file=$1
  diff -u $file'Ac.ref.hpp' $file'Ac.hpp' && \
  diff -u $file'Ac.ref.h' $file'Ac.h'
}

diff_hpp()
{
  if test $# -ne 1
  then
    echo 'usage: diff_hpp file' 1>&2
    exit 1
  fi
  file=$1
  diff -u $file'Ac.ref.hpp' $file'Ac.hpp'
}

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

component()
{
  run_test "-p $PWD" component && \
    diff_cpp C_AArray && \
    diff_cpp CComponent && \
    diff_h_hpp TAlias && \
    diff_hpp C_TAlias
}
