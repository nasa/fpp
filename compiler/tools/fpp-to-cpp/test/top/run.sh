basic()
{
  cd basic
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
  cd ..
}

commands()
{
  cd commands
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp CommandsTopology
  cd ..
}

health()
{
  cd health
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
  run_test "-i builtin.fpp -p $PWD" health && \
    diff_cpp HealthTopology
  cd ..
}

nested_namespaces()
{
  run_test "-p $PWD" nested_namespaces && \
    diff_cpp NestedNamespacesTopology
}

no_namespace()
{
  run_test "-p $PWD" no_namespace && \
    diff_cpp NoNamespaceTopology
}

params()
{
  run_test "-i builtin.fpp -p $PWD" params && \
    diff_cpp ParamsTopology
}

tlm_packets()
{
  run_test "-i builtin.fpp -p $PWD" tlm_packets && \
    diff_cpp NoInstancesTopology && \
    diff_cpp NoInstances_P1TlmPackets && \
    diff_cpp NoInstances_P2TlmPackets && \
    diff_cpp OneInstanceTopology && \
    diff_cpp OneInstance_P1TlmPackets && \
    diff_cpp OneInstance_P2TlmPackets && \
    diff_cpp OneInstance_P3TlmPackets && \
    diff_cpp TwoInstancesTopology && \
    diff_cpp TwoInstances_P1TlmPackets
}

typed_ports_active()
{
  src_dir=$PWD/typed_ports_active
  run_test "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology && \
    diff_cpp $src_dir/TypedPortsActiveTopology
}

typed_ports_passive()
{
  src_dir=$PWD/typed_ports_passive
  run_test "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology && \
    diff_cpp $src_dir/TypedPortsPassiveTopology
}

typed_ports_queued()
{
  src_dir=$PWD/typed_ports_queued
  run_test "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology && \
    diff_cpp $src_dir/TypedPortsQueuedTopology
}
