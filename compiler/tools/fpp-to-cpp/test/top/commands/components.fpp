module M {

  passive component C {

    command recv port cmdIn

    command reg port cmdRegOut

    command resp port cmdResponseOut

    sync command C

  }

  passive component CmdDispatcher {

    output port cmdOut: Fw.Cmd

    sync input port cmdRegIn: Fw.CmdReg

    sync input port cmdResponseIn: Fw.CmdResponse

  }

  passive component NoCommands {

    command recv port cmdIn

    command reg port cmdRegOut

    command resp port cmdResponseOut

  }

}
