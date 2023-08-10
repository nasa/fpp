component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  run_test "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../empty" empty && \
  diff_test_template Empty
}

passive()
{
  run_test "-u -t -i `cat ../deps-comma.txt`" "-p $PWD,$fprime_dir ../passive" passive && \
  diff_test_template PassiveTest && \
  diff_test_template PassiveSerial && \
  diff_test_template PassiveCommands && \
  diff_test_template PassiveEvents && \
  diff_test_template PassiveTelemetry && \
  diff_test_template PassiveParams
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
