basic()
{
  cd $TOP_DIR/basic
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology
  mv basic.names.txt basic.names.ref.txt
  move_cpp BasicTopology
}

commands()
{
  cd $TOP_DIR/commands
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp CommandsTopology
}

dp()
{
  cd $TOP_DIR/dp
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp DpTopology
}

events()
{
  cd $TOP_DIR/events
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp EventsTopology
}

health()
{
  cd $TOP_DIR/health
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp HealthTopology
}

nested_namespaces()
{
  cd $TOP_DIR/nested_namespaces
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp NestedNamespacesTopology
}

no_namespace()
{
  cd $TOP_DIR/no_namespace
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp" topology
  move_cpp NoNamespaceTopology
}

params()
{
  cd $TOP_DIR/params
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp ParamsTopology
}

tlm()
{
  cd $TOP_DIR/tlm
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp TlmTopology
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
}

typed_ports_active()
{
  cd $TOP_DIR
  src_dir=$PWD/typed_ports_active
  update "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology
  move_cpp $src_dir/TypedPortsActiveTopology
}

typed_ports_passive()
{
  cd $TOP_DIR
  src_dir=$PWD/typed_ports_passive
  update "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology
  move_cpp $src_dir/TypedPortsPassiveTopology
}

typed_ports_queued()
{
  cd $TOP_DIR
  src_dir=$PWD/typed_ports_queued
  update "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology
  move_cpp $src_dir/TypedPortsQueuedTopology
}
