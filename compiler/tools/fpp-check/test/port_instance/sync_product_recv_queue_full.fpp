module Fw {

  port DpBufferSend

}

passive component C {

  sync product recv port productRecvIn drop

}
