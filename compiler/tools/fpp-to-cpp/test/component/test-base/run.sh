component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  # Run the empty test in auto setup mode
  # This tests auto setup
  run_test "-a -u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty && \
  diff_test Empty
}

passive()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive && \
  diff_test PassiveCommands && \
  diff_test PassiveEvents && \
  diff_test PassiveGetProductPortsOnly && \
  diff_test PassiveGetProducts && \
  diff_test PassiveGuardedProducts && \
  diff_test PassiveParams && \
  diff_test PassiveSerial && \
  diff_test PassiveSyncProductPortsOnly && \
  diff_test PassiveSyncProducts && \
  diff_test PassiveTelemetry && \
  diff_test PassiveTest
}

active()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active && \
  diff_test ActiveAsyncProducts && \
  diff_test ActiveAsyncProductPortsOnly && \
  diff_test ActiveCommands && \
  diff_test ActiveEvents && \
  diff_test ActiveGetProducts && \
  diff_test ActiveGuardedProducts && \
  diff_test ActiveNoArgsPortsOnly && \
  diff_test ActiveParams &&\
  diff_test ActiveSerial && \
  diff_test ActiveSyncProducts && \
  diff_test ActiveTelemetry && \
  diff_test ActiveTest
}

queued()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued && \
  diff_test QueuedAsyncProducts && \
  diff_test QueuedAsyncProductPortsOnly && \
  diff_test QueuedCommands && \
  diff_test QueuedEvents && \
  diff_test QueuedGetProducts && \
  diff_test QueuedGuardedProducts && \
  diff_test QueuedNoArgsPortsOnly && \
  diff_test QueuedParams && \
  diff_test QueuedSerial && \
  diff_test QueuedSyncProducts
  diff_test QueuedTelemetry && \
  diff_test QueuedTest
}
