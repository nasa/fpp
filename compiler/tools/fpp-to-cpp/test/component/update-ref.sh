fprime_dir=`dirname $PWD`/fprime

active()
{
  update "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir active" active
  move_cpp TypedPort 
  move_cpp EEnum
  move_cpp AArray
  move_cpp SSerializable
  move_cpp ActiveComponent
}
