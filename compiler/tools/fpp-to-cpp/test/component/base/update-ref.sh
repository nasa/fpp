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
  move_cpp PassiveTestComponent
  move_cpp PassiveSerialComponent
  move_cpp PassiveCommandsComponent
  move_cpp PassiveEventsComponent
  move_cpp PassiveTelemetryComponent
  move_cpp PassiveParamsComponent
}

active()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_cpp ActiveTestComponent
  move_cpp ActiveSerialComponent
  move_cpp ActiveCommandsComponent
  move_cpp ActiveEventsComponent
  move_cpp ActiveTelemetryComponent
  move_cpp ActiveParamsComponent
}

queued()
{
  update "-i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_cpp QueuedTestComponent
  move_cpp QueuedSerialComponent
  move_cpp QueuedCommandsComponent
  move_cpp QueuedEventsComponent
  move_cpp QueuedTelemetryComponent
  move_cpp QueuedParamsComponent
}
