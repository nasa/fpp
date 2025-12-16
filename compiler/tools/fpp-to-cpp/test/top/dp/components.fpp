module M {

  queued component C {

    time get port timeGetOut

    product get port productGetOut

    product request port productRequestOut

    async product recv port productRecvIn

    product send port productSendOut

    product record R: U32

    product container C

  }

  queued component DpManager {

    sync input port productGetIn: Fw.DpGet

    sync input port productSendIn: Fw.DpSend

    output port productResponseOut: Fw.DpResponse

    async input port productRequestIn: Fw.DpRequest

  }

  passive component NoDp {

    time get port timeGetOut

    product get port productGetOut

    product send port productSendOut

  }

}
