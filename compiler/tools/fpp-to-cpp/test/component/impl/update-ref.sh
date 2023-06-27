component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  update "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty
  move_template Empty
}

passive()
{
  update "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive
  move_template PassiveTest
  move_template PassiveSerial
  move_template PassiveCommands
  move_template PassiveEvents
  move_template PassiveTelemetry
  move_template PassiveParams
}

active()
{
  update "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_template ActiveTest
  move_template ActiveSerial
  move_template ActiveCommands
  move_template ActiveEvents
  move_template ActiveTelemetry
  move_template ActiveParams
}

queued()
{
  update "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_template QueuedTest
  move_template QueuedSerial
  move_template QueuedCommands
  move_template QueuedEvents
  move_template QueuedTelemetry
  move_template QueuedParams
}
