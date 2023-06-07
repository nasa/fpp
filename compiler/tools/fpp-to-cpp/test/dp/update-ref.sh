fprime_dir=`dirname $PWD`/fprime

dp()
{
  update "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir dp" dp
  move_cpp DpTestComponent
  move_cpp DpTest_DataSerializable
}
