default()
{
  update "-p $PWD" default
  move_cpp DefaultEnum
}

explicit()
{
  update "-p $PWD" explicit
  move_cpp ExplicitEnum
}

implicit()
{
  update "-p $PWD" implicit
  move_cpp ImplicitEnum
}

serialize_type()
{
  update "-p $PWD" serialize_type
  move_cpp SerializeTypeEnum
}

component()
{
  update "-p $PWD" component
  move_cpp C_EEnum
  move_cpp EEnum
}
