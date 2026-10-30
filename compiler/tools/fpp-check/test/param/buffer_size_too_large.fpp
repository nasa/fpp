constant FW_PARAM_BUFFER_MAX_SIZE = 8

module Fw {

  port Cmd
  port CmdReg
  port CmdResponse

  port PrmGet
  port PrmSet

}

array F64x3 = [3] F64

passive component C {

  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdResponseOut

  param get port prmGetOut
  param set port prmSetOut

  param P: F64x3

}
