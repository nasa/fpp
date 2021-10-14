constants()
{
  update "-n constants.names.txt -p $PWD" constants
  mv constants.names.txt constants.names.ref.txt
  move_cpp FppConstants _constants
}

constants_guard_dir()
{
  dir=`cd ../../..; echo $PWD`
  update "-p $dir" constants
  move_cpp FppConstants _constants_guard_dir
}

constants_guard_prefix()
{
  update "-g GUARD_PREFIX -p $PWD" constants
  move_cpp FppConstants _constants_guard_prefix
}

constants_output_dir()
{
  update "-d output_dir -p $PWD" constants output_dir/constants
  move_cpp output_dir/FppConstants
}

constants_string()
{
  update "-p $PWD" constants_string
  move_cpp FppConstants _constants_string
}
