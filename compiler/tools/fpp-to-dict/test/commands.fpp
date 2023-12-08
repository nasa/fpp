module M {
  constant a1 = 1
}
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
  @ Command annotation on newline
  async command COMMAND_1 priority 10

  @ Command with formal parameters and priority
  async command COMMAND_2(
    a: U32 @< description of arg a
    b: F32 @< description of arg b
  ) priority 20

  @ Command with formal parameters, opcode, priority, and queue full behavior
  async command COMMAND_3(a: string size 10) opcode 0x10 priority 30 drop

  sync command COMMAND_4(a: string)

  sync command COMMAND_5(a: bool)

  sync command COMMAND_6(a: M.E1)

  sync command COMMAND_7(a: M.M2.E2)
}

module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}

module M {
  enum E1 { X = 0, Y = 1 }
  array A1 = [3] E1 # E is a qualified identifier type name. It names the type M.E.
  struct S1 {
    x: U32
    y: F32
  }
  
  module M2 {
    enum E2 { X = 0, Y = 1 }
    array A2 = [3] E2 # E is a qualified identifier type name. It names the type M.E.
    struct S2 {
      x: U32
      y: F32
    }
    struct T2 {
      s: S2 # S is a qualified identifier type name. It names the type M.S.
    }
  }
}