module M {

  port P

  passive component C1 {

    output port pOut: [4] P
    sync input port pIn: [4] P

    match pOut with pIn
  }

  instance c1: C1 base id 0x100
  instance c2: C1 base id 0x200
  instance c3: C1 base id 0x300
  instance c4: C1 base id 0x300
  instance c5: C1 base id 0x300

  topology T1 {

    instance c1
    instance c2
    instance c3
    instance c4
    # instance c5

    connections P {
      c1.pOut -> c2.pIn
      c2.pOut -> c1.pIn[0]
      unmatched c1.pOut[0] -> c3.pIn

      c3.pOut -> c4.pIn
      c4.pOut -> c3.pIn
      unmatched c4.pOut[0] -> c3.pIn
      

      # c1.pOut -> c2.pIn
      # c2.pOut -> c1.pIn[0]
      # unmatched c2.pOut -> c3.pIn
    }
  }

}
