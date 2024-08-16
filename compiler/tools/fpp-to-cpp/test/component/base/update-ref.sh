component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

types()
{
  update "-p $component_dir" "../types" types
  move_cpp NoArgsPort
  move_cpp NoArgsReturnPort
  move_cpp TypedPort 
  move_cpp TypedReturnPort
  move_cpp EEnum
  move_cpp AArray
  move_cpp SSerializable
}

empty()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty
  move_cpp EmptyComponent
}

passive()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive
  move_cpp PassiveCommandsComponent
  move_cpp PassiveEventsComponent
  move_cpp PassiveGetProductPortsOnlyComponent
  move_cpp PassiveGetProductsComponent
  move_cpp PassiveGuardedProductsComponent
  move_cpp PassiveParamsComponent
  move_cpp PassiveSerialComponent
  move_cpp PassiveSyncProductPortsOnlyComponent
  move_cpp PassiveSyncProductsComponent
  move_cpp PassiveTelemetryComponent
  move_cpp PassiveTestComponent
}

active()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_cpp ActiveAsyncProductPortsOnlyComponent
  move_cpp ActiveAsyncProductsComponent
  move_cpp ActiveCommandsComponent
  move_cpp ActiveOverflowComponent
  move_cpp ActiveEventsComponent
  move_cpp ActiveGetProductsComponent
  move_cpp ActiveGuardedProductsComponent
  move_cpp ActiveNoArgsPortsOnlyComponent
  move_cpp ActiveParamsComponent
  move_cpp ActiveSerialComponent
  move_cpp ActiveStateMachinesComponent
  move_cpp ActiveSyncProductsComponent
  move_cpp ActiveTelemetryComponent
  move_cpp ActiveTestComponent
}

queued()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_cpp QueuedAsyncProductPortsOnlyComponent
  move_cpp QueuedAsyncProductsComponent
  move_cpp QueuedCommandsComponent
  move_cpp QueuedOverflowComponent
  move_cpp QueuedEventsComponent
  move_cpp QueuedGetProductsComponent
  move_cpp QueuedGuardedProductsComponent
  move_cpp QueuedNoArgsPortsOnlyComponent
  move_cpp QueuedParamsComponent
  move_cpp QueuedSerialComponent
  move_cpp QueuedSyncProductsComponent
  move_cpp QueuedTelemetryComponent
  move_cpp QueuedTestComponent
}
