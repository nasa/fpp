module M {

  passive component SpecialPorts {

    command recv port cmdIn
    command reg port cmdRegOut
    command resp port cmdResponseOut

    event port eventOut
    text event port textEventOut

    param get port paramGetOut
    param set port paramSetOut

    telemetry port tlmOut

    product get port productGetOut
    product request port productRequestOut
    sync product recv port productRecvIn
    product send port productSendOut

  }

}
