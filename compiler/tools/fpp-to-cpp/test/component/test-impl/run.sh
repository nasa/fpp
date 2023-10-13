component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  # Run the empty test in auto setup mode
  # This tests auto setup
  run_test "-a -u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../empty" empty && \
  diff_test_template Empty
}

passive()
{
  run_test "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../passive" passive && \
  diff_test_template PassiveCommands && \
  diff_test_template PassiveEvents && \
  diff_test_template PassiveGetPortsOnly && \
  diff_test_template PassiveGetProducts && \
  diff_test_template PassiveGuardedProducts && \
  diff_test_template PassiveParams && \
  diff_test_template PassiveSerial && \
  diff_test_template PassiveSyncPortsOnly && \
  diff_test_template PassiveSyncProducts && \
  diff_test_template PassiveTelemetry && \
  diff_test_template PassiveTest
}

active()
{
  run_test "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../active" active && \
  diff_test_template ActiveTest && \
  diff_test_template ActiveSerial && \
  diff_test_template ActiveCommands && \
  diff_test_template ActiveEvents && \
  diff_test_template ActiveTelemetry && \
  diff_test_template ActiveParams
}

queued()
{
  run_test "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../queued" queued && \
  diff_test_template QueuedTest && \
  diff_test_template QueuedSerial && \
  diff_test_template QueuedCommands && \
  diff_test_template QueuedEvents && \
  diff_test_template QueuedTelemetry && \
  diff_test_template QueuedParams
}
