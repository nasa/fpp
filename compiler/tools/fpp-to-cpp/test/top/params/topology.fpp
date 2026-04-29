module M {

  instance c1: C base id 0x100 {

    phase Phases.readParameters """
    M::c1.readParamFile();
    """

    phase Phases.loadParameters """
    M::c1.loadParamsSpecial();
    """

  }

  instance c2: C base id 0x200

  instance c3: NoParams base id 0x300

  topology Params {

    instance c1
    instance c2
    instance c3

  }

}
