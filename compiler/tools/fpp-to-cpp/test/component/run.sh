fprime_dir=`dirname $PWD`/fprime

types()
{
  run_test "-p $PWD" types && \
  diff_cpp TypedPort && \
  diff_cpp EEnum && \
  diff_cpp AArray && \
  diff_cpp SSerializable
}

empty()
{
  run_test "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir empty" empty && \
  diff_cpp EmptyComponent
}

passive()
{
  run_test "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir passive" passive && \
  diff_cpp PassiveComponent && \
  diff_cpp PassiveCommandsComponent && \
  diff_cpp PassiveEventsComponent && \
  diff_cpp PassiveTelemetryComponent && \
  diff_cpp PassiveParamsComponent
}

active()
{
  run_test "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir active" active && \
  diff_cpp ActiveComponent && \
  diff_cpp ActiveCommandsComponent && \
  diff_cpp ActiveEventsComponent && \
  diff_cpp ActiveTelemetryComponent && \
  diff_cpp ActiveParamsComponent
}

queued()
{
  run_test "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir queued" queued && \
  diff_cpp QueuedComponent && \
  diff_cpp QueuedCommandsComponent && \
  diff_cpp QueuedEventsComponent && \
  diff_cpp QueuedTelemetryComponent && \
  diff_cpp QueuedParamsComponent
}
