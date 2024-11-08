module M {

  port P

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
  
  topology T {

    instance c1
    instance c2

    connections P {

      c1.pOut[0] -> c2.pIn
      c2.pOut -> c1.pIn
      unmatched c2.pOut -> c1.pIn[0]

    }

  }

}
