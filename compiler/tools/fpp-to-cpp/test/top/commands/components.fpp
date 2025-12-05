module M {

  passive component C {

    command recv port cmdOut

    command reg port cmdRegOut

    command resp port cmdResponseOut

    sync command C

  }

  passive component NoCommands {

    command recv port cmdOut

    command reg port cmdRegOut

    command resp port cmdResponseOut

  }

}
