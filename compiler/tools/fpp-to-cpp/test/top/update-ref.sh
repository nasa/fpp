basic()
{
  cd basic
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology
  mv basic.names.txt basic.names.ref.txt
  move_cpp BasicTopology
  cd ..
}

commands()
{
  cd commands
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp CommandsTopology
  cd ..
}

health()
{
  cd health
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp HealthTopology
  cd ..
}

nested_namespaces()
{
  cd nested_namespaces
  update "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology
  move_cpp NestedNamespacesTopology
  cd ..
}

no_namespace()
{
  update "-p $PWD" no_namespace
  move_cpp NoNamespaceTopology
}

params()
{
  update "-i builtin.fpp -p $PWD" params
  move_cpp ParamsTopology
}

tlm_packets()
{
  update "-i builtin.fpp -p $PWD" tlm_packets
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
  src_dir=$PWD/typed_ports_active
  update "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology
  move_cpp $src_dir/TypedPortsActiveTopology
}

typed_ports_passive()
{
  src_dir=$PWD/typed_ports_passive
  update "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology
  move_cpp $src_dir/TypedPortsPassiveTopology
}

typed_ports_queued()
{
  src_dir=$PWD/typed_ports_queued
  update "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology
  move_cpp $src_dir/TypedPortsQueuedTopology
}
