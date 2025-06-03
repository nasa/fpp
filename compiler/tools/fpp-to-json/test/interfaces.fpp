module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}

port General

@ An interface for illustrating importing special and general ports
@ into a component.
interface ICmd {
  # ----------------------------------------------------------------------
  # Commanding Ports
  # ----------------------------------------------------------------------

  @ Command registration
  command reg port cmdRegOut

  @ Command input
  command recv port cmdIn

  @ Command response
  command resp port cmdResponseOut
}

interface IPort {
  async input port general: General
}

active component PriorityQueueFull {
  import ICmd
  import IPort

  # ----------------------------------------------------------------------
  # Commands
  # ----------------------------------------------------------------------

  @ Command with priority
  async command COMMAND_1 priority 10

  @ Command with formal parameters and priority
  async command COMMAND_2(a: U32, b: F32) priority 20

  @ Command with formal parameters, opcode, priority, and queue full behavior
  async command COMMAND_3(a: string) opcode 0x10 priority 30 drop

}