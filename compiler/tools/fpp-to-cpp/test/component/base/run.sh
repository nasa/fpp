component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

types()
{
  run_test "-p $component_dir" "../types" types && \
  diff_cpp NoArgsPort && \
  diff_cpp NoArgsReturnPort && \
  diff_cpp TypedPort && \
  diff_cpp TypedReturnPort && \
  diff_cpp EEnum && \
  diff_cpp AArray && \
  diff_cpp SSerializable
}

empty()
{
  run_test "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty && \
  diff_cpp EmptyComponent
}

passive()
{
  run_test "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive && \
  diff_cpp PassiveCommandsComponent && \
  diff_cpp PassiveEventsComponent && \
  diff_cpp PassiveGetProductPortsOnlyComponent && \
  diff_cpp PassiveGetProductsComponent && \
  diff_cpp PassiveGuardedProductsComponent && \
  diff_cpp PassiveParamsComponent &&\
  diff_cpp PassiveSerialComponent && \
  diff_cpp PassiveSyncProductPortsOnlyComponent && \
  diff_cpp PassiveSyncProductsComponent && \
  diff_cpp PassiveTelemetryComponent && \
  diff_cpp PassiveTestComponent
}

active()
{
  run_test "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active && \
  diff_cpp ActiveAsyncProductPortsOnlyComponent && \
  diff_cpp ActiveAsyncProductsComponent && \
  diff_cpp ActiveCommandsComponent && \
  diff_cpp ActiveOverflowComponent && \
  diff_cpp ActiveEventsComponent && \
  diff_cpp ActiveGetProductsComponent && \
  diff_cpp ActiveGuardedProductsComponent && \
  diff_cpp ActiveNoArgsPortsOnlyComponent && \
  diff_cpp ActiveParamsComponent && \
  diff_cpp ActiveSerialComponent && \
  diff_cpp ActiveStateMachinesComponent && \
  diff_cpp ActiveSyncProductsComponent && \
  diff_cpp ActiveTelemetryComponent && \
  diff_cpp ActiveTestComponent
}

queued()
{
  run_test "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued && \
  diff_cpp QueuedAsyncProductPortsOnlyComponent && \
  diff_cpp QueuedAsyncProductsComponent && \
  diff_cpp QueuedCommandsComponent && \
  diff_cpp QueuedOverflowComponent && \
  diff_cpp QueuedEventsComponent && \
  diff_cpp QueuedGetProductsComponent && \
  diff_cpp QueuedGuardedProductsComponent && \
  diff_cpp QueuedNoArgsPortsOnlyComponent && \
  diff_cpp QueuedParamsComponent && \
  diff_cpp QueuedSerialComponent && \
  diff_cpp QueuedSyncProductsComponent && \
  diff_cpp QueuedTelemetryComponent && \
  diff_cpp QueuedTestComponent
}
