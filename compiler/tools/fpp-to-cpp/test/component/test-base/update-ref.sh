component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  # Run the empty test in auto setup mode
  # This tests auto setup
  update "-a -u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty
  move_test Empty
}

passive()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive
  move_test PassiveCommands
  move_test PassiveEvents
  move_test PassiveGetProductPortsOnly
  move_test PassiveGetProducts
  move_test PassiveGuardedProducts
  move_test PassiveParams
  move_test PassiveSerial
  move_test PassiveSyncProductPortsOnly
  move_test PassiveSyncProducts
  move_test PassiveTelemetry
  move_test PassiveTest
}

active()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active
  move_test ActiveAsyncProducts
  move_test ActiveAsyncProductPortsOnly
  move_test ActiveCommands
  move_test ActiveEvents
  move_test ActiveGetProducts
  move_test ActiveGuardedProducts
  move_test ActiveNoArgsPortsOnly
  move_test ActiveParams
  move_test ActiveSerial
  move_test ActiveSyncProducts
  move_test ActiveTelemetry
  move_test ActiveTest
}

queued()
{
  update "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued
  move_test QueuedAsyncProducts
  move_test QueuedAsyncProductPortsOnly
  move_test QueuedCommands
  move_test QueuedEvents
  move_test QueuedGetProducts
  move_test QueuedGuardedProducts
  move_test QueuedNoArgsPortsOnly
  move_test QueuedParams
  move_test QueuedSerial
  move_test QueuedSyncProducts
  move_test QueuedTelemetry
  move_test QueuedTest
}
