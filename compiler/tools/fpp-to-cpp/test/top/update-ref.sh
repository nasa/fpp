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
  update "-p $PWD" health
  move_cpp HealthTopology
}

params()
{
  update "-i builtin.fpp -p $PWD" params
  move_cpp ParamsTopology
}

