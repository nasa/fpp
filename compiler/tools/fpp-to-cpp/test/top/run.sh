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

