module Fw {

  port DpResponse

}

passive component C {

  sync product recv port productRecvIn drop

}
