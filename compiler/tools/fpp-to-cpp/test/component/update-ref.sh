fprime_dir=`dirname $PWD`/fprime

empty()
{
  update "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir empty" empty
  move_cpp EmptyComponent
}

passive()
{
  update "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir types.fpp passive" passive
  move_cpp TypedPort 
  move_cpp EEnum
  move_cpp AArray
  move_cpp SSerializable
  move_cpp PassiveComponent
}

active()
{
  update "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir active" active
  move_cpp ActiveComponent
}

queued()
{
  update "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir queued" queued
  move_cpp QueuedComponent
}
