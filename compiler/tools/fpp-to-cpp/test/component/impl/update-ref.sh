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
  move_template PassiveCommands
  move_template PassiveEvents
  move_template PassiveGetContainersOnly
  move_template PassiveGetPortsOnly
  move_template PassiveGetProducts
  move_template PassiveGetRecordsOnly
  move_template PassiveGuardedProducts
  move_template PassiveParams
  move_template PassiveSerial
  move_template PassiveSyncContainersOnly
  move_template PassiveSyncPortsOnly
  move_template PassiveSyncProducts
  move_template PassiveSyncRecordsOnly
  move_template PassiveTelemetry
  move_template PassiveTest
}

active()
{
  update "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_template ActiveCommands
  move_template ActiveEvents
  move_template ActiveGetProducts
  move_template ActiveGuardedProducts
  move_template ActiveParams
  move_template ActiveSerial
  move_template ActiveSyncProducts
  move_template ActiveTelemetry
  move_template ActiveTest
}

queued()
{
  update "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_template QueuedCommands
  move_template QueuedEvents
  move_template QueuedGetProducts
  move_template QueuedGuardedProducts
  move_template QueuedParams
  move_template QueuedSerial
  move_template QueuedSyncProducts
  move_template QueuedTelemetry
  move_template QueuedTest
}
