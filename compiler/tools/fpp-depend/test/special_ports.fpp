locate port Fw.Cmd at "Cmd.fpp"
locate port Fw.CmdReg at "CmdReg.fpp"
locate port Fw.CmdResponse at "CmdResponse.fpp"
locate port Fw.Log at "Log.fpp"
locate port Fw.PrmGet at "PrmGet.fpp"
locate port Fw.PrmSet at "PrmSet.fpp"
locate port Fw.DpRequest at "DpRequest.fpp"
locate port Fw.DpResponse at "DpResponse.fpp"
locate port Fw.DpSend at "DpSend.fpp"
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

  async product recv port productRecvIn

  product request port productRequestOut

  product send port productSendOut

  telemetry port tlmOut

  text event port textEventOut

  time get port timeGetOut

}
