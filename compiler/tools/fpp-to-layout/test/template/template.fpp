module template T(constant baseId: U32) {

  port P

  passive component C1 {

    output port pOut: P

  }

  passive component C2 {

    sync input port pIn: P

  }

  instance c1: C1 base id baseId
  instance c2: C2 base id baseId + 0x100

  topology Top {

    instance c1
    instance c2

    connections C {
      c1.pOut -> c2.pIn
    }

  }

}

module M {
  expand T(constant 0x100)
}
