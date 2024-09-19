module M {

  port P

  passive component C1 {

    output port pOut: [6] P
    sync input port pIn: [6] P

    match pOut with pIn
  }

  passive component C2 {

    output port pOut: [6] P
    sync input port pIn: [6] P

    match pOut with pIn
  }

  instance c1: C1 base id 0x100
  instance c2: C1 base id 0x200
  instance c3: C2 base id 0x300
  instance c4: C2 base id 0x400

  topology T {

    instance c1
    instance c2
    instance c3
    instance c4

    connections P {
      # Case 1: 2 ports that go to the same component
      unmatched c1.pOut -> c1.pIn
      unmatched c2.pOut -> c1.pIn
      unmatched c3.pOut -> c3.pIn
      unmatched c4.pOut -> c4.pIn

      # Case 2: Have a connection out of A at some index i and
      # some connection at B at same index i but they go to different components 
      unmatched c1.pOut[1] -> c1.pIn[1]
      unmatched c2.pOut[1] -> c2.pIn[1]


      # Case 3: Have a connection out of A at index i and
      # some connection at B index at j not equal to i going to same component
      unmatched c3.pOut[2] -> c3.pIn[2]
      unmatched c4.pOut[3] -> c3.pIn[3]

      # Case 4: Have a connection out of A at index i and
      # some connection at B index at j not equal to i going to different component
      unmatched c3.pOut[4] -> c3.pIn[4]
      unmatched c4.pOut[5] -> c4.pIn[5]

    }
  }

}
