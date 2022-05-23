#!/bin/sh

fpp_depend=../../../../bin/fpp-depend

port_kwd_name()
{
  run_test "-p $PWD" port_kwd_name && \
    diff_xml PortKwdNamePort
}

port_ok()
{
  files=""
  for i in `seq 1 4`; do files="$files PortOK${i}Port"; done
  run_test "-i `$fpp_depend port_ok.fpp | tr '\n' ','` -p $PWD" port_ok && \
    diff_xml $files
}

types()
{
  for file in A E S
  do
    run_test "-p $PWD" $file
  done
}

