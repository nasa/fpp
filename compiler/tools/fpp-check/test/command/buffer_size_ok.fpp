type FwSizeStoreType = U16

constant FW_CMD_ARG_BUFFER_MAX_SIZE = 6

module Fw {

  port Cmd
  port CmdReg
  port CmdResponse

}

passive component C {

  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut

  @ 4 bytes for x, plus at least 2 bytes for s
  sync command C(x: U32, s: string size 100)

}
