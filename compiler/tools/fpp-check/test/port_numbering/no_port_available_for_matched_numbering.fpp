module M {

  port P

  passive component C1 {

    output port pOut: [2] P
    sync input port pIn: [2] P

    match pOut with pIn

  }

  passive component C2 {

    output port pOut: [2] P
    sync input port pIn: [2] P

  }

  passive component C3 {

    output port pOut: P
    sync input port pIn: P

  }

  instance c1: C1 base id 0x100
  instance c2: C2 base id 0x200
  instance c3: C2 base id 0x300

  topology T {

    instance c1
    instance c2
    instance c3

    connections P {

      unmatched c1.pOut[0] -> c3.pIn
      unmatched c3.pOut -> c1.pIn[1]

      c1.pOut -> c2.pIn
      c2.pOut -> c1.pIn

    }

  }

}
