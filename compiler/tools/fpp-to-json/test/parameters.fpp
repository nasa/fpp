@ An array of 3 F64 values
array F64x3 = [3] F64

@ A component for illustrating parameter set and save opcodes
passive component ParamOpcodes {

  # ----------------------------------------------------------------------
  # Ports
  # ----------------------------------------------------------------------

  @ Command receive port
  command recv port cmdIn

  @ Command registration port
  command reg port cmdRegOut

  @ Command response port
  command resp port cmdResponseOut

  @ Parameter get port
  param get port prmGetOut

  @ Parameter set port
  param set port prmSetOut

  # ----------------------------------------------------------------------
  # Parameters
  # ----------------------------------------------------------------------

  @ Parameter 1
  @ Its implied set opcode is 0x00
  @ Its implied save opcode is 0x01
  param Param1: U32 default 1

  @ Parameter 2
  @ Its set opcode is 0x10
  @ Its save opcode is 0x11
  param Param2: F64 \
    default 2.0 \
    id 0x10 \
    set opcode 0x10 \
    save opcode 0x11

  @ Parameter 3
  @ Its set opcode is 0x12
  @ Its save opcode is 0x20
  param Param3: F64x3 \
    default [ 1.0, 2.0, 3.0 ] \
    save opcode 0x20

}

module Fw {
  port PrmGet
  port PrmSet
  port Cmd
  port CmdReg
  port CmdResponse
}
