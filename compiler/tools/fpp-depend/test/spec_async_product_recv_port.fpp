locate port Fw.DpBufferSend at "DpBufferSend.fpp"
locate constant a at "a.fpp"

active component C {

  async product recv port p priority a

}
