basic()
{
  cd $TOP_DIR/basic
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
  cd $TOP_DIR
}

commands()
{
  cd $TOP_DIR/commands
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp CommandsTopology
  cd $TOP_DIR
}

dp()
{
  cd $TOP_DIR/dp
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp DpTopology
  cd $TOP_DIR
}

events()
{
  cd $TOP_DIR/events
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp EventsTopology
  cd $TOP_DIR
}

health()
{
  cd $TOP_DIR/health
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp HealthTopology
  cd $TOP_DIR
}

nested_namespaces()
{
  cd $TOP_DIR/nested_namespaces
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp NestedNamespacesTopology
  cd $TOP_DIR
}

no_namespace()
{
  cd $TOP_DIR/no_namespace
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp" topology && \
    diff_cpp NoNamespaceTopology
  cd $TOP_DIR
}

params()
{
  cd $TOP_DIR/params
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp ParamsTopology
  cd $TOP_DIR
}

tlm()
{
  cd $TOP_DIR/tlm
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp TlmTopology
  cd $TOP_DIR
}

ports()
{
  run_test "-i builtin.fpp -n ports.names.txt -p $PWD" ports && \
    diff -u ports.names.txt ports.names.ref.txt && \
    diff_cpp Ports1Topology && \
    diff_cpp Ports2Topology
}

tlm_packets()
{
  cd $TOP_DIR/tlm_packets
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp NoInstancesTopology && \
    diff_cpp NoInstances_P1TlmPackets && \
    diff_cpp NoInstances_P2TlmPackets && \
    diff_cpp OneInstanceTopology && \
    diff_cpp OneInstance_P1TlmPackets && \
    diff_cpp OneInstance_P2TlmPackets && \
    diff_cpp OneInstance_P3TlmPackets && \
    diff_cpp TwoInstancesTopology && \
    diff_cpp TwoInstances_P1TlmPackets
  cd $TOP_DIR
}

typed_ports_active()
{
  cd $TOP_DIR/typed_ports_active
  run_test "-p $PWD -i $FPRIME_DEPS components.fpp" topology && \
    diff_cpp TypedPortsActiveTopology
  cd $TOP_DIR
}

typed_ports_passive()
{
  cd $TOP_DIR/typed_ports_passive
  run_test "-p $PWD components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp TypedPortsPassiveTopology
  cd $TOP_DIR
}

typed_ports_queued()
{
  cd $TOP_DIR/typed_ports_queued
  run_test "-p $PWD components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp TypedPortsQueuedTopology
  cd $TOP_DIR
}
