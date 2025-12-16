module M {

  instance c1: C base id 0x100 {

    phase Phases.regCommands """
    M::c1.regCommandsSpecial();
    """

  }

  instance c2: C base id 0x200

  instance cmdDispatcher: CmdDispatcher base id 0x300

  instance noCommands: NoCommands base id 0x400

  topology Commands {

    instance c1
    instance c2
    instance cmdDispatcher
    instance noCommands

    connections Commands {
      c1.cmdRegOut -> cmdDispatcher.cmdRegIn
      cmdDispatcher.cmdOut -> c1.cmdIn
      c1.cmdResponseOut -> cmdDispatcher.cmdResponseIn
    }

  }

}
