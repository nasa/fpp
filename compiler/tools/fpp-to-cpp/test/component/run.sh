fprime_dir=`dirname $PWD`/fprime

empty()
{
  run_test "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir empty" empty && \
  diff_cpp EmptyComponent
}

passive()
{
  run_test "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir types.fpp passive" passive && \
  diff_cpp TypedPort && \
  diff_cpp EEnum && \
  diff_cpp AArray && \
  diff_cpp SSerializable && \
  diff_cpp PassiveComponent
}

active()
{
  run_test "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir active" active && \
  diff_cpp ActiveComponent
}

queued()
{
  run_test "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir queued" queued && \
  diff_cpp QueuedComponent
}
