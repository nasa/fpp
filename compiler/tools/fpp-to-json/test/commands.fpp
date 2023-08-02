@ A component for illustrating priority and queue full behavior for async
@ commands
active component PriorityQueueFull {

  # ----------------------------------------------------------------------
  # Ports
  # ----------------------------------------------------------------------

  @ Command registration
  command reg port cmdRegOut

  @ Command input
  command recv port cmdIn

  @ Command response
  command resp port cmdResponseOut

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

module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}
