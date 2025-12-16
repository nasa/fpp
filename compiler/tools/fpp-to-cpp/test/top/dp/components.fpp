module M {

  passive component C {

    time get port timeGetOut

    product get port productGetOut

    product send port productSendOut

    product record R: U32

    product container C

  }

  passive component DpManager {

    sync input port productGetIn: Fw.DpGet

    sync input port productSendIn: Fw.DpSend

  }

  passive component NoDp {

    time get port timeGetOut

    product get port productGetOut

    product send port productSendOut

  }

}
