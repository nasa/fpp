module Fw {

  port Cmd
  port CmdReg
  port CmdResponse

  port PrmGet
  port PrmSet

}

array U32x3 = [3] U32

passive component C {

  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut

  param get port prmGetOut
  param set port prmSetOut

  @ Parameter P1
  param P1: U32 \
    id 0x00 \
    set opcode 0x80 \
    save opcode 0x81

  @ Parameter P2
  param P2: F64 \
    default 1.0 \
    id 0x01 \
    set opcode 0x82 \
    save opcode 0x83

  @ Parameter P3
  param P3: U32x3 \
    default 0
    
  @ Parameter P4
  param P4: string

}
