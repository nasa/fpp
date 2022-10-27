passive component Parameters {

  command recv port cmdIn

  command reg port cmdRegOut

  command resp port cmdResponseOut

  param get port paramGetOut

  param set port paramSetOut

  @ Parameter P1
  param P1: U32
  param P2: F32 set opcode 0x100 save opcode 0x101
  param P3: I32 default 10
  param P4: string default "a\"bc"

}
