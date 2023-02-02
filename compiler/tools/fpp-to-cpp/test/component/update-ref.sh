fprime_dir=`dirname $PWD`/fprime

types()
{
  update "-p $PWD" types
  move_cpp TypedPort 
  move_cpp EEnum
  move_cpp AArray
  move_cpp SSerializable
}

empty()
{
  update "-i `cat deps-comma.txt`" "-p $PWD,$fprime_dir empty" empty
  move_cpp EmptyComponent
}

passive()
{
  update "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir passive" passive
  move_cpp PassiveComponent
  move_cpp PassiveCommandsComponent
  move_cpp PassiveEventsComponent
  move_cpp PassiveTelemetryComponent
  move_cpp PassiveParamsComponent
}

active()
{
  update "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir active" active
  move_cpp ActiveComponent
  move_cpp ActiveCommandsComponent
  move_cpp ActiveEventsComponent
  move_cpp ActiveTelemetryComponent
  move_cpp ActiveParamsComponent
}

queued()
{
  update "-i `cat deps-comma.txt`,types.fpp" "-p $PWD,$fprime_dir queued" queued
  move_cpp QueuedComponent
  move_cpp QueuedCommandsComponent
  move_cpp QueuedEventsComponent
  move_cpp QueuedTelemetryComponent
  move_cpp QueuedParamsComponent
}
