alias_serial_type()
{
  update "-p $PWD" alias_serial_type
  move_cpp AliasSerialTypeEnum
}

component()
{
  update "-p $PWD" component
  move_cpp C_EEnum
  move_cpp EEnum
}

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

singleton_range()
{
  update "-p $PWD" singleton_range
  move_cpp SingletonRangeEnum
}

serialize_type()
{
  update "-p $PWD" serialize_type
  move_cpp SerializeTypeEnum
}

state_machine()
{
  update "-p $PWD" state_machine
  move_cpp SM_EEnum
}
