module Fw {

  port Cmd
  port CmdReg
  port CmdResponse

}

active component C {

  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut

  @ A sync command with no parameters
  sync command SyncNoParams opcode 0x00

  @ An async command with no parameters
  async command AsyncNoParams opcode 0x01

  @ A sync command with parameters
  sync command SyncParams(
      param1: U32 @< Param 1
      param2: string @< Param 2
  ) opcode 0x02

  @ An async command with parameters
  async command AsyncParams(
      param1: U32 @< Param 1
      param2: string @< Param 2
  ) opcode 0x03

  @ An async command with priority
  async command AsyncPriority(
      param1: U32 @< Param 1
      param2: string @< Param 2
  ) opcode 0x04 priority 10

  @ An async command with priority and drop on queue full
  async command AsyncPriorityDrop(
      param1: U32 @< Param 1
      param2: string @< Param 2
  ) opcode 0x05 priority 10 drop

}
