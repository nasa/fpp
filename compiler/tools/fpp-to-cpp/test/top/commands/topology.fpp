module M {

  instance c1: C base id 0x100 {

    phase Phases.regCommands """
    M::c1.regCommandsSpecial();
    """

  }

  instance c2: C base id 0x200

  instance c3: NoCommands base id 0x300

  topology Commands {

    instance c1
    instance c2
    instance c3

  }

}
