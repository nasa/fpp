#!/bin/sh

port_kwd_name()
{
  update "-p $PWD" port_kwd_name
  move_xml PortKwdNamePort
}

port_ok()
{
  update "-i `fpp-depend port_ok.fpp | tr '\n' ','`  -p $PWD" port_ok
  move_xml PortOK1Port PortOK2Port PortOK3Port PortOK4Port
}

types()
{
  for file in A E S
  do
    update "-p $PWD" $file
  done
  move_xml AArray EEnum SSerializable
}
