fprime_dir=`dirname $PWD`/fprime

dp()
{
  run_test "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir dp" dp && \
  diff_cpp DpTestDpComponent && \
  diff_cpp DpTest_DataSerializable
}
