constants()
{
  run_test "-n constants.names.txt -p $PWD" constants && \
    diff -u constants.names.txt constants.names.ref.txt && \
    diff_cpp FppConstants _constants
}

constants_guard_dir()
{
  dir=`cd ../../..; echo $PWD`
  run_test "-p $dir" constants && \
    diff_cpp FppConstants _constants_guard_dir
}

constants_guard_prefix()
{
  run_test "-g GUARD_PREFIX -p $PWD" constants && \
    diff_cpp FppConstants _constants_guard_prefix
}

constants_output_dir()
{
  run_test "-d output_dir -p $PWD" constants output_dir/constants && \
    diff_cpp output_dir/FppConstants
}

constants_string()
{
  run_test "-p $PWD" constants_string constants_string && \
    diff_cpp FppConstants _constants_string
}
