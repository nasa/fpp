basic()
{
  update "-i builtin.fpp -n basic.names.txt -p $PWD" basic
  mv basic.names.txt basic.names.ref.txt
  move_cpp BasicTopology
}

commands()
{
  update "-i builtin.fpp -p $PWD" commands
  move_cpp CommandsTopology
}

health()
{
  update "-i builtin.fpp -p $PWD" health
  move_cpp HealthTopology
}

nested_namespaces()
{
  update "-p $PWD" nested_namespaces
  move_cpp NestedNamespacesTopology
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

