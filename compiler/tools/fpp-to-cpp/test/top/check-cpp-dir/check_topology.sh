check_topology()
{

  top_dir=$1
  top_name=$2

  dir=`dirname $0`
  dir=`cd $dir; pwd`

  cd $dir/../..
  . ./defs.sh
  cd $dir

  echo '  removing old files'
  ./clean

  echo '  generating C++'
  (
    cd $dir/../../$top_dir;
    $FPP_TO_CPP -p $PWD -i $FPRIME_DEPS,../phases.fpp -d $dir components.fpp topology.fpp
  )

  flags="-I$FPRIME_DIR -I$FPRIME_DIR/fprime/config -Wno-unused-parameter -c"
  echo '  compiling C++'
  for variable_flags in '' '-DFW_DIRECT_PORT_CALLS'
  do
    echo "    variable_flags=$variable_flags"
    $FPRIME_GCC $variable_flags $flags $top_name'TopologyAc.cpp'
  done

}
