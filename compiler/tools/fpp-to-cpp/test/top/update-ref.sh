basic()
{
  update "-n basic.names.txt -p $PWD" basic
  mv basic.names.txt basic.names.ref.txt
  move_cpp BasicTopology
}
