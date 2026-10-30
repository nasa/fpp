constant FW_CMD_ARG_BUFFER_MAX_SIZE = 8

module Fw {

  port Cmd
  port CmdReg
  port CmdResponse

}

array A = [3] U32

passive component C {

  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut

  sync command C(a: A)

}
