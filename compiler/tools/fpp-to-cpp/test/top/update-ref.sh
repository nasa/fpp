basic()
{
  update "-n basic.names.txt -p $PWD" basic
  mv basic.names.txt basic.names.ref.txt
  move_cpp BasicTopology
}

health()
{
  update "-p $PWD" health
  move_cpp HealthTopology
}

