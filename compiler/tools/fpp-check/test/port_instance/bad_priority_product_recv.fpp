module Fw {

  port DpBufferSend

}

active component C {

  async product recv port productRecvIn priority "abc"

}
