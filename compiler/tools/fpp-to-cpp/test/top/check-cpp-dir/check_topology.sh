check_topology()
{

  src_dir=$1

  dir=`dirname $0`
  dir=`cd $dir; pwd`

  cd $dir/../..
  . ./defs.sh
  cd $dir

  echo '  removing old files'
  ./clean

  echo '  generating C++'
  (
    cd $dir/../../$src_dir;
    $FPP_TO_CPP -p $PWD -i $FPRIME_DEPS,../phases.fpp -d $dir components.fpp topology.fpp
  )

  options="
  -I..
  -I$FPRIME_DIR
  -I$FPRIME_DIR/config
  -I$FPRIME_DIR/Fw/Time
  -I$FPRIME_DIR/Fw/Tlm
  -Wno-unused-parameter
  -c
  "

  #flags="-I$FPRIME_DIR -I$FPRIME_DIR/config -Wno-unused-parameter -c"
  top_files=`find . -maxdepth 1 -name '*TopologyAc.cpp'`
  for top_file in $top_files
  do
    top_name=`echo $top_file | sed -e 's;^\./;;' -e 's/TopologyAc\.cpp$//'`
    echo '  compiling C++ for '$top_name
    for variable_flags in '' '-DFW_DIRECT_PORT_CALLS'
    do
      echo "    variable_flags=$variable_flags"
      $FPRIME_GCC $variable_flags $options $top_name'TopologyAc.cpp'
    done
  done

  tlm_packet_files=`find . -maxdepth 1 -name '*TlmPacketsAc.cpp'`
  for tlm_packet_file in $tlm_packet_files
  do
    echo "    compiling $tlm_packet_file"
    for variable_flags in '' '-DFW_DIRECT_PORT_CALLS'
    do
      echo "      variable_flags=$variable_flags"
      $FPRIME_GCC $variable_flags $options $tlm_packet_file
    done
  done

}
