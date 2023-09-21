component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

types()
{
  update "-u -p $component_dir" "../types" types
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
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty
  move_test Empty
}

passive()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive
  move_test PassiveCommands
  move_test PassiveEvents
  move_test PassiveGetProducts
  move_test PassiveGuardedProducts
  move_test PassiveParams
  move_test PassiveSerial
  move_test PassiveSyncProducts
  move_test PassiveTelemetry
  move_test PassiveTest
}

active()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_test ActiveTest
  move_test ActiveSerial
  move_test ActiveCommands
  move_test ActiveEvents
  move_test ActiveTelemetry
  move_test ActiveParams
}

queued()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_test QueuedTest
  move_test QueuedSerial
  move_test QueuedCommands
  move_test QueuedEvents
  move_test QueuedTelemetry
  move_test QueuedParams
}
