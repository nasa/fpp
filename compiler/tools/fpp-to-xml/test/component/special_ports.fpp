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

  }

}
