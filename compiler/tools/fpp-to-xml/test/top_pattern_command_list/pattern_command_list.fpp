module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}
module M {
  passive component Commands {
    sync input port cmdRegIn: Fw.CmdReg
    output port cmdOut: Fw.Cmd
    sync input port cmdResponseIn: Fw.CmdResponse
    match cmdRegIn with cmdOut
    match cmdRegIn with cmdResponseIn
  }
  passive component C {
    command reg port cmdRegOut
    command recv port cmdIn
    command resp port cmdResponseOut
  }
  instance commands: Commands base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x300
  topology T {
    instance commands
    instance c1
    instance c2
    command connections instance commands { c1 }
  }
}
