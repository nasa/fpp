locate port Fw.DpResponse at "DpResponse.fpp"
locate constant a at "a.fpp"

active component C {

  async product recv port p priority a

}
