module Fw {

  port DpResponse

}

active component C {

  async product recv port productRecvIn priority "abc"

}
