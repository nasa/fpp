module template T(constant baseId: U32) {

  passive component C {

    @ A telemetry channel
    telemetry Channel: U32

    @ A command
    sync command DoIt

    @ An event
    event Happened severity activity high format "it happened"

    command reg port cmdRegOut
    command recv port cmdIn
    command resp port cmdResponseOut
    event port logOut
    text event port logTextOut
    telemetry port tlmOut
    time get port timeGetOut

  }

  instance c: C base id baseId

  topology Sub {
    instance c
  }

}

module Dep {

  expand T(constant 0x100)

  deployment topology Expanded {
    import Sub
  }

  system Expanded: Expanded

}
