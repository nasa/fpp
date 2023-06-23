component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  update "-t -i `cat deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty
  move_cpp_no_ac Empty
}

passive()
{
  update "-t -i `cat deps-comma.txt`,../types.fpp" "-p $PWD,$fprime_dir ../passive" passive
  move_cpp_no_ac PassiveTest
  move_cpp_no_ac PassiveSerial
  move_cpp_no_ac PassiveCommands
  move_cpp_no_ac PassiveEvents
  move_cpp_no_ac PassiveTelemetry
  move_cpp_no_ac PassiveParams
}

active()
{
  update "-t -i `cat deps-comma.txt`,../types.fpp" "-p $PWD,$fprime_dir ../active" active
  move_cpp_no_ac ActiveTest
  move_cpp_no_ac ActiveSerial
  move_cpp_no_ac ActiveCommands
  move_cpp_no_ac ActiveEvents
  move_cpp_no_ac ActiveTelemetry
  move_cpp_no_ac ActiveParams
}

queued()
{
  update "-t -i `cat deps-comma.txt`,../types.fpp" "-p $PWD,$fprime_dir ../queued" queued
  move_cpp_no_ac QueuedTest
  move_cpp_no_ac QueuedSerial
  move_cpp_no_ac QueuedCommands
  move_cpp_no_ac QueuedEvents
  move_cpp_no_ac QueuedTelemetry
  move_cpp_no_ac QueuedParams
}
