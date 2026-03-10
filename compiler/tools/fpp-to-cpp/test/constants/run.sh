. ./fpp-options.sh

constants()
{
  run_test "$fpp_import_option -n constants.names.txt -p $PWD" constants && \
    diff -u constants.names.txt constants.names.ref.txt && \
    diff_cpp_suffix FppConstants _constants
}

constants_guard_dir()
{
  dir=`cd ../../..; echo $PWD`
  run_test "$fpp_import_option -p $dir" constants && \
    diff_cpp_suffix FppConstants _constants_guard_dir
}

constants_guard_prefix()
{
  run_test "$fpp_import_option -g GUARD_PREFIX -p $PWD" constants && \
    diff_cpp_suffix FppConstants _constants_guard_prefix
}

constants_output_dir()
{
  run_test "$fpp_import_option -d output_dir -p $PWD" constants output_dir/constants && \
    diff_cpp_suffix output_dir/FppConstants
}

constants_string()
{
  run_test "$fpp_import_option -p $PWD" constants_string constants_string && \
    diff_cpp_suffix FppConstants _constants_string
}
