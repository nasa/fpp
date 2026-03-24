basic()
{
  cd $TOP_DIR/basic
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
  status=$?
  cd $TOP_DIR
  return $status
}

commands()
{
  cd $TOP_DIR/commands
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp CommandsTopology
  status=$?
  cd $TOP_DIR
  return $status
}

dp()
{
  cd $TOP_DIR/dp
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp DpTopology
  status=$?
  cd $TOP_DIR
  return $status
}

events()
{
  cd $TOP_DIR/events
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp EventsTopology
  status=$?
  cd $TOP_DIR
  return $status
}

health()
{
  cd $TOP_DIR/health
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp HealthTopology
  status=$?
  cd $TOP_DIR
  return $status
}

nested_namespaces()
{
  cd $TOP_DIR/nested_namespaces
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp NestedNamespacesTopology
  status=$?
  cd $TOP_DIR
  return $status
}

no_namespace()
{
  cd $TOP_DIR/no_namespace
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp" topology && \
    diff_cpp NoNamespaceTopology
  status=$?
  cd $TOP_DIR
  return $status
}

params()
{
  cd $TOP_DIR/params
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp ParamsTopology
  status=$?
  cd $TOP_DIR
  return $status
}

serial_ports_active()
{
  cd $TOP_DIR/serial_ports_active
  run_test "-p $PWD,$FPRIME_DIR components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp SerialPortsActiveTopology
  status=$?
  cd $TOP_DIR
  return $status
}

serial_ports_passive()
{
  cd $TOP_DIR/serial_ports_passive
  run_test "-p $PWD,$FPRIME_DIR components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp SerialPortsPassiveTopology
  status=$?
  cd $TOP_DIR
  return $status
}

serial_ports_queued()
{
  cd $TOP_DIR/serial_ports_queued
  run_test "-p $PWD,$FPRIME_DIR components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp SerialPortsQueuedTopology
  status=$?
  cd $TOP_DIR
  return $status
}

tlm()
{
  cd $TOP_DIR/tlm
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp TlmTopology
  status=$?
  cd $TOP_DIR
  return $status
}

ports()
{
  cd $TOP_DIR/ports
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp -n names.txt" topology && \
    diff -u names.txt names.ref.txt && \
    diff_cpp Ports1Topology && \
    diff_cpp Ports2Topology
  status=$?
  cd $TOP_DIR
  return $status
}

tlm_packets()
{
  cd $TOP_DIR/tlm_packets
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp NoInstancesTopology && \
    diff_cpp NoInstances_P1TlmPackets && \
    diff_cpp NoInstances_P2TlmPackets && \
    diff_cpp OneInstanceTopology && \
    diff_cpp OneInstance_P1TlmPackets && \
    diff_cpp OneInstance_P2TlmPackets && \
    diff_cpp OneInstance_P3TlmPackets && \
    diff_cpp TwoInstancesTopology && \
    diff_cpp TwoInstances_P1TlmPackets
  status=$?
  cd $TOP_DIR
  return $status
}

typed_ports_active()
{
  cd $TOP_DIR/typed_ports_active
  run_test "-p $PWD,$FPRIME_DIR -i $FPRIME_DEPS components.fpp" topology && \
    diff_cpp TypedPortsActiveTopology
  status=$?
  cd $TOP_DIR
  return $status
}

typed_ports_passive()
{
  cd $TOP_DIR/typed_ports_passive
  run_test "-p $PWD,$FPRIME_DIR components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp TypedPortsPassiveTopology
  status=$?
  cd $TOP_DIR
  return $status
}

typed_ports_queued()
{
  cd $TOP_DIR/typed_ports_queued
  run_test "-p $PWD,$FPRIME_DIR components.fpp -i $FPRIME_DEPS" topology && \
    diff_cpp TypedPortsQueuedTopology
  status=$?
  cd $TOP_DIR
  return $status
}
