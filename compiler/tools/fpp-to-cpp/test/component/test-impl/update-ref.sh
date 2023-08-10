component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  update "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../empty" empty
  move_test_template Empty
}

passive()
{
  update "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../passive" passive
  move_test_template PassiveTest
  move_test_template PassiveSerial
  move_test_template PassiveCommands
  move_test_template PassiveEvents
  move_test_template PassiveTelemetry
  move_test_template PassiveParams
}

active()
{
  update "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../active" active
  move_test_template ActiveTest
  move_test_template ActiveSerial
  move_test_template ActiveCommands
  move_test_template ActiveEvents
  move_test_template ActiveTelemetry
  move_test_template ActiveParams
}

queued()
{
  update "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../queued" queued
  move_test_template QueuedTest
  move_test_template QueuedSerial
  move_test_template QueuedCommands
  move_test_template QueuedEvents
  move_test_template QueuedTelemetry
  move_test_template QueuedParams
}
