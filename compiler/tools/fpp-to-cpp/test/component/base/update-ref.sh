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
  move_cpp PassiveGetContainersOnlyComponent
  move_cpp PassiveGetPortsOnlyComponent
  move_cpp PassiveGetProductsComponent
  move_cpp PassiveGetRecordsOnlyComponent
  move_cpp PassiveGuardedProductsComponent
  move_cpp PassiveParamsComponent
  move_cpp PassiveSerialComponent
  move_cpp PassiveSyncContainersOnlyComponent
  move_cpp PassiveSyncPortsOnlyComponent
  move_cpp PassiveSyncProductsComponent
  move_cpp PassiveSyncRecordsOnlyComponent
  move_cpp PassiveTelemetryComponent
  move_cpp PassiveTestComponent
}

active()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_cpp ActiveAsyncProductsComponent
  move_cpp ActiveCommandsComponent
  move_cpp ActiveEventsComponent
  move_cpp ActiveGetProductsComponent
  move_cpp ActiveGuardedProductsComponent
  move_cpp ActiveParamsComponent
  move_cpp ActiveSerialComponent
  move_cpp ActiveSyncProductsComponent
  move_cpp ActiveTelemetryComponent
  move_cpp ActiveTestComponent
}

queued()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_cpp QueuedAsyncProductsComponent
  move_cpp QueuedCommandsComponent
  move_cpp QueuedEventsComponent
  move_cpp QueuedGetProductsComponent
  move_cpp QueuedGuardedProductsComponent
  move_cpp QueuedParamsComponent
  move_cpp QueuedSerialComponent
  move_cpp QueuedSyncProductsComponent
  move_cpp QueuedTelemetryComponent
  move_cpp QueuedTestComponent
}
