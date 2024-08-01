module M {

    # enums
    enum E1: U32 {
        X = 0
        Y = 1
        Z = 2
    } default X

    passive component C {

        command recv port cmdOut

        command reg port cmdRegOut

        command resp port cmdResponseIn

        param P: M.E1

        param get port prmGetOut

        param set port prmSetOut
    }

    instance I1: C base id 0x100
}

topology QualifiedCompInst {

    instance M.I1

}

instance I2: M.C base id 0x300

topology UnqualifiedCompInst {

    instance I2

}
