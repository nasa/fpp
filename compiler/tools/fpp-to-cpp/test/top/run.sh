basic()
{
  cd $TOP_DIR/basic
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp -n basic.names.txt" topology && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
}

commands()
{
  cd $TOP_DIR/commands
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp CommandsTopology
}

health()
{
  cd $TOP_DIR/health
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp HealthTopology
}

nested_namespaces()
{
  cd $TOP_DIR/nested_namespaces
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp NestedNamespacesTopology
}

no_namespace()
{
  cd $TOP_DIR/no_namespace
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp" topology && \
    diff_cpp NoNamespaceTopology
}

params()
{
  cd $TOP_DIR/params
  run_test "-p $PWD -i $FPRIME_DEPS,../phases.fpp,components.fpp" topology && \
    diff_cpp ParamsTopology
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
}

typed_ports_active()
{
  cd $TOP_DIR
  src_dir=$PWD/typed_ports_active
  run_test "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology && \
    diff_cpp $src_dir/TypedPortsActiveTopology
}

typed_ports_passive()
{
  cd $TOP_DIR
  src_dir=$PWD/typed_ports_passive
  run_test "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology && \
    diff_cpp $src_dir/TypedPortsPassiveTopology
}

typed_ports_queued()
{
  cd $TOP_DIR
  src_dir=$PWD/typed_ports_queued
  run_test "-d $src_dir -p $PWD,$src_dir -i $src_dir/components.fpp" \
    $src_dir/topology && \
    diff_cpp $src_dir/TypedPortsQueuedTopology
}
