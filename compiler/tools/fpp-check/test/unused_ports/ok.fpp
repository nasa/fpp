module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}

port P

passive component C {
  sync input port pIn: P
  output port pOut: P
  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut
}

instance c: C base id 0x100

topology T {
  unused {
    c.pIn
    c.pOut
    c.cmdIn
    c.cmdRegOut
    c.cmdResponseOut
  }
}
