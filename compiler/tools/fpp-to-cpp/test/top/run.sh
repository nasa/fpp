basic()
{
  run_test "-i builtin.fpp -n basic.names.txt -p $PWD" basic && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
}

commands()
{
  run_test "-i builtin.fpp -p $PWD" commands && \
    diff_cpp CommandsTopology
}

health()
{
  run_test "-i builtin.fpp -p $PWD" health && \
    diff_cpp HealthTopology
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

typed_ports()
{
  run_test "-p $PWD" typed_ports && \
    diff_cpp TypedPortsTopology
}
