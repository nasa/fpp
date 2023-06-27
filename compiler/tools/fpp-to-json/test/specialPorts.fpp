@ A component for illustrating command ports
passive component CommandPorts {

  @ A port for receiving commands
  command recv port cmdIn

  @ A port for sending command registration requests
  command reg port cmdRegOut

  @ A port for sending command responses
  command resp port cmdResponseOut

}

@ A component for illustrating event ports
passive component EventPorts {

  @ A port for emitting events
  event port eventOut

  @ A port for emitting text events
  text event port textEventOut

}

@ A component for illustrating telemetry ports
passive component TelemetryPorts {

  @ A port for emitting telemetry
  telemetry port tlmOut

}

@ A component for illustrating parameter ports
passive component ParamPorts {

  @ A port for getting parameter values
  param get port prmGetOut

  @ A port for setting parameter values
  param set port prmSetOut

}

@ A component for illustrating time get ports
passive component TimeGetPorts {

  @ A port for getting the time
  time get port timeGetOut

} 

module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
  port Log
  port LogText
  port Tlm
  port PrmGet
  port PrmSet
  port Time
}
