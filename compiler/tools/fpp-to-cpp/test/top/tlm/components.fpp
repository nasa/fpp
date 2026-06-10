module M {

  passive component C {

    time get port timeGetOut

    telemetry port tlmOut

    telemetry T: U32

  }

  passive component TlmManager {

    sync input port tlmIn: Fw.Tlm

  }

  passive component NoTlm {

    time get port timeGetOut

    telemetry port tlmOut

  }

}
