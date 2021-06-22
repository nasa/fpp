module M {

  passive component C {

    command recv port cmdOut

    command reg port cmdRegOut

    command resp port cmdResponseIn

    sync command C

  }

  instance c1: C base id 0x100
  instance c2: C base id 0x200

  init c1 phase Phases.regCommands """
  c1.regCommandsSpecial();
  """

  topology Commands {

    instance c1
    instance c2

  }

}
