component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime
test_dir=`dirname $component_dir`

empty()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../empty" empty && \
  diff_template Empty
}

passive()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../passive" passive && \
  diff_template PassiveCommands && \
  diff_template PassiveEvents && \
  diff_template PassiveGetProductPortsOnly && \
  diff_template PassiveGetProducts && \
  diff_template PassiveGuardedProducts && \
  diff_template PassiveParams && \
  diff_template PassiveSerial && \
  diff_template PassiveSyncProductPortsOnly && \
  diff_template PassiveSyncProducts && \
  diff_template PassiveTelemetry && \
  diff_template PassiveTest
}

active()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../active" active && \
  diff_template ActiveAsyncProductPortsOnly && \
  diff_template ActiveAsyncProducts && \
  diff_template ActiveCommands && \
  diff_template ActiveOverflow && \
  diff_template ActiveEvents && \
  diff_template ActiveExternalStateMachines && \
  diff_template ActiveGetProducts && \
  diff_template ActiveGuardedProducts && \
  diff_template ActiveNoArgsPortsOnly && \
  diff_template ActiveParams && \
  diff_template ActiveSerial && \
  diff_template ActiveSyncProducts && \
  diff_template ActiveTelemetry && \
  diff_template ActiveTest
}

queued()
{
  run_test "-t -i `cat ../deps-comma.txt`" "-p $component_dir,$fprime_dir ../queued" queued && \
  diff_template QueuedAsyncProductPortsOnly && \
  diff_template QueuedAsyncProducts && \
  diff_template QueuedCommands && \
  diff_template QueuedOverflow && \
  diff_template QueuedEvents && \
  diff_template QueuedGetProducts && \
  diff_template QueuedGuardedProducts && \
  diff_template QueuedNoArgsPortsOnly && \
  diff_template QueuedParams && \
  diff_template QueuedSerial && \
  diff_template QueuedSyncProducts && \
  diff_template QueuedTelemetry && \
  diff_template QueuedTest
}

sm_choice()
{
  run_test "-t -i `cat ../deps-comma.txt`,`cat ../sm-deps-comma.txt`" "-p $component_dir,$fprime_dir,$test_dir ../sm_choice" sm_choice && \
  diff_template SmChoiceActive && \
  diff_template SmChoiceQueued
}

sm_initial()
{
  run_test "-t -i `cat ../deps-comma.txt`,`cat ../sm-deps-comma.txt`" "-p $component_dir,$fprime_dir,$test_dir ../sm_initial" sm_initial && \
  diff_template SmInitialActive && \
  diff_template SmInitialQueued
}

sm_state()
{
  run_test "-t -i `cat ../deps-comma.txt`,`cat ../sm-deps-comma.txt`" "-p $component_dir,$fprime_dir,$test_dir ../sm_state" sm_state && \
  diff_template SmStateActive && \
  diff_template SmStateQueued
}
