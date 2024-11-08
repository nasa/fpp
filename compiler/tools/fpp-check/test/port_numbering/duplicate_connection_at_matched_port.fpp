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

  instance c1: C1 base id 0x100
  instance c2: C1 base id 0x200

  topology T1 {

    instance c1
    instance c2

    connections P {

      c2.pOut -> c1.pIn[0]
      c2.pOut -> c1.pIn[0]

    }

  }

}
