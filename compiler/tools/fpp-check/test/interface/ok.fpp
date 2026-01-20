port P()

interface I1 {
  import I2
}

interface I2 {
}

module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}

interface Cmd {
    sync input port cmdRegIn: Fw.CmdReg
    output port cmdOut: Fw.Cmd
    sync input port cmdResponseIn: Fw.CmdResponse
}

interface I {
    async input port pAsync: P
    sync input port pSync: P
    guarded input port pGuarded: P
    output port pOut: P

    async input port pAsyncArray: [10] P
    sync input port pSyncArray: [10] P
    guarded input port pGuardedArray: [10] P
    output port pOutArray: [10] P
}

active component C {
    import Cmd
    import I
    import I3
}

interface I3 {
    async input port pAsync2: P
}
