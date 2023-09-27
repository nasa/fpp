component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty
  move_test Empty
}

passive()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive
  move_test PassiveTest
  move_test PassiveSerial
  move_test PassiveCommands
  move_test PassiveEvents
  move_test PassiveTelemetry
  move_test PassiveParams
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
