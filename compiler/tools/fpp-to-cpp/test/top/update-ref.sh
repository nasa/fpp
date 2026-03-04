basic()
{
  cd $TOP_DIR/basic
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology
  mv basic.names.txt basic.names.ref.txt
  move_cpp BasicTopology
  cd $TOP_DIR
}

commands()
{
  cd $TOP_DIR/commands
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp CommandsTopology
  cd $TOP_DIR
}

dp()
{
  cd $TOP_DIR/dp
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp DpTopology
  cd $TOP_DIR
}

events()
{
  cd $TOP_DIR/events
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp EventsTopology
  cd $TOP_DIR
}

health()
{
  cd $TOP_DIR/health
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp HealthTopology
  cd $TOP_DIR
}

nested_namespaces()
{
  cd $TOP_DIR/nested_namespaces
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp NestedNamespacesTopology
  cd $TOP_DIR
}

no_namespace()
{
  cd $TOP_DIR/no_namespace
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp" topology
  move_cpp NoNamespaceTopology
  cd $TOP_DIR
}

params()
{
  cd $TOP_DIR/params
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp ParamsTopology
  cd $TOP_DIR
}

tlm()
{
  cd $TOP_DIR/tlm
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp TlmTopology
  cd $TOP_DIR
}

tlm_packets()
{
  cd $TOP_DIR/tlm_packets
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp NoInstancesTopology
  move_cpp NoInstances_P1TlmPackets
  move_cpp NoInstances_P2TlmPackets
  move_cpp OneInstanceTopology
  move_cpp OneInstance_P1TlmPackets
  move_cpp OneInstance_P2TlmPackets
  move_cpp OneInstance_P3TlmPackets
  move_cpp TwoInstancesTopology
  move_cpp TwoInstances_P1TlmPackets
  cd $TOP_DIR
}

typed_ports_active()
{
  cd $TOP_DIR/typed_ports_active
  update "-p $PWD components.fpp -i $FPRIME_DEPS" topology
  move_cpp TypedPortsActiveTopology
  cd $TOP_DIR
}

typed_ports_passive()
{
  cd $TOP_DIR/typed_ports_passive
  update "-p $PWD components.fpp -i $FPRIME_DEPS" topology
  move_cpp TypedPortsPassiveTopology
  cd $TOP_DIR
}

typed_ports_queued()
{
  cd $TOP_DIR/typed_ports_queued
  update "-p $PWD components.fpp -i $FPRIME_DEPS" topology
  move_cpp TypedPortsQueuedTopology
  cd $TOP_DIR
}
