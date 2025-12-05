module M {

  passive component C {

    command recv port cmdOut

    command reg port cmdRegOut

    command resp port cmdResponseIn

    param P: U32

    param get port prmGetOut

    param set port prmSetOut

  }

}
