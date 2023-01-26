fprime_dir=`dirname $PWD`/fprime

active()
{
  run_test "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir active" active && \
  diff_cpp TypedPort && \
  diff_cpp EEnum && \
  diff_cpp AArray && \
  diff_cpp SSerializable && \
  diff_cpp ActiveComponent
}
