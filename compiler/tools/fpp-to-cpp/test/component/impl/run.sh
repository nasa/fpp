component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

empty()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty && \
  diff_template Empty
}

passive()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive && \
  diff_template PassiveTest && \
  diff_template PassiveSerial && \
  diff_template PassiveCommands && \
  diff_template PassiveEvents && \
  diff_template PassiveTelemetry && \
  diff_template PassiveParams
}

active()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active && \
  diff_template ActiveTest && \
  diff_template ActiveSerial && \
  diff_template ActiveCommands && \
  diff_template ActiveEvents && \
  diff_template ActiveTelemetry && \
  diff_template ActiveParams
}

queued()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued && \
  diff_template QueuedTest && \
  diff_template QueuedSerial && \
  diff_template QueuedCommands && \
  diff_template QueuedEvents && \
  diff_template QueuedTelemetry && \
  diff_template QueuedParams
}
