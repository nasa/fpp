component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty && \
  diff_test Empty
}

passive()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive && \
  diff_test PassiveTest && \
  diff_test PassiveSerial && \
  diff_test PassiveCommands && \
  diff_test PassiveEvents && \
  diff_test PassiveTelemetry && \
  diff_test PassiveParams
}

active()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active && \
  diff_test ActiveTest && \
  diff_test ActiveSerial && \
  diff_test ActiveCommands && \
  diff_test ActiveEvents && \
  diff_test ActiveTelemetry && \
  diff_test ActiveParams
}

queued()
{
  run_test "-u -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued && \
  diff_test QueuedTest && \
  diff_test QueuedSerial && \
  diff_test QueuedCommands && \
  diff_test QueuedEvents && \
  diff_test QueuedTelemetry && \
  diff_test QueuedParams
}
