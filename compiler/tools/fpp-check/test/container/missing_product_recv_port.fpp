module Fw {
  port DpResponse
  port DpRequest
  port DpSend
  port Time
}

passive component C {

  product container C

  product request port productRequestOut

  product send port productSendOut

  time get port timeGetOut

}
