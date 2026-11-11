module M {

  port P

  interface I {
    output port pOut: [4] P
    sync input port pIn: [4] P
  }

  passive component C1 {

    output port pOut: [4] P
    sync input port pIn: [4] P

    match pOut with pIn

  }

  passive component C2 {

    output port pOut: [4] P
    sync input port pIn: [4] P

  }

  instance c1: C1 base id 0x100
  instance c2: C2 base id 0x200

  module template T(instance i: I) {

    topology Top {

      instance i
      instance c2

      connections P {

        i.pOut[0] -> c2.pIn
        c2.pOut -> i.pIn
        unmatched c2.pOut -> i.pIn[0]

      }

    }

  }

  expand T(instance c1)

}
