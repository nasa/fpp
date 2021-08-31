module M {

  passive component C {

    command recv port cmdOut

    command reg port cmdRegOut

    command resp port cmdResponseIn

    param P: U32

    param get port prmGetOut

    param set port prmSetOut

  }

  instance c1: C base id 0x100 {

    phase Phases.readParameters """
    c1.readParamFile();
    """

    phase Phases.loadParameters """
    c1.loadParamsSpecial();
    """

  }

  instance c2: C base id 0x200

  topology Params {

    instance c1
    instance c2

  }

}
