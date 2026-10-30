type FwSizeStoreType = U16

constant FW_PARAM_BUFFER_MAX_SIZE = 2

module Fw {

  port Cmd
  port CmdReg
  port CmdResponse

  port PrmGet
  port PrmSet

}

passive component C {

  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut

  param get port prmGetOut
  param set port prmSetOut

  @ At least 2 bytes, for an empty string
  param P: string size 100

}
