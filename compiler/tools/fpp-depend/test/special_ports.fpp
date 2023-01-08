locate port Fw.Cmd at "Cmd.fpp"
locate port Fw.CmdReg at "CmdReg.fpp"
locate port Fw.CmdResponse at "CmdResponse.fpp"
locate port Fw.Log at "Log.fpp"
locate port Fw.PrmGet at "PrmGet.fpp"
locate port Fw.PrmSet at "PrmSet.fpp"
locate port Fw.DpBufferRequest at "DpBufferRequest.fpp"
locate port Fw.DpBufferSend at "DpBufferSend.fpp"
locate port Fw.Tlm at "Tlm.fpp"
locate port Fw.LogText at "LogText.fpp"
locate port Fw.Time at "Time.fpp"

active component C {

  command recv port cmdIn

  command reg port cmdRegIn

  command resp port cmdResponseIn

  event port eventOut

  param get port prmGetOut

  param set port prmSetOut

  product request port productRequestOut

  product send port productSendOut

  telemetry port tlmOut

  text event port textEventOut
  
  time get port timeGetOut

}
