check_topology()
{

  if test -z "$1"
  then
    src_dir=`basename $PWD`
  else
    src_dir=$1
  fi

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
  variable_flag_array='
  -DFW_DIRECT_PORT_CALLS=0:-DFW_ENABLE_TEXT_LOGGING=0
  -DFW_DIRECT_PORT_CALLS=0:-DFW_ENABLE_TEXT_LOGGING=1
  -DFW_DIRECT_PORT_CALLS=1:-DFW_ENABLE_TEXT_LOGGING=0
  -DFW_DIRECT_PORT_CALLS=1:-DFW_ENABLE_TEXT_LOGGING=1
  '
  top_files=`find . -maxdepth 1 -name '*TopologyAc.cpp'`
  for top_file in $top_files
  do
    top_name=`echo $top_file | sed -e 's;^\./;;' -e 's/TopologyAc\.cpp$//'`
    echo '  compiling C++ for '$top_name
    for variable_flags in $variable_flag_array
    do
      variable_flags=`echo $variable_flags | sed 's/:/ /g'`
      echo "    variable_flags=$variable_flags"
      $FPRIME_GCC $variable_flags $options $top_name'TopologyAc.cpp'
    done
  done

  tlm_packet_files=`find . -maxdepth 1 -name '*TlmPacketsAc.cpp'`
  for tlm_packet_file in $tlm_packet_files
  do
    echo "    compiling $tlm_packet_file"
    for variable_flags in $variable_flag_array
    do
      variable_flags=`echo $variable_flags | sed 's/:/ /g'`
      echo "      variable_flags=$variable_flags"
      $FPRIME_GCC $variable_flags $options $tlm_packet_file
    done
  done

}
