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
  instance c4: C1 base id 0x400
  instance c5: C1 base id 0x500
  instance c6: C1 base id 0x600
  instance c7: C1 base id 0x700
  instance c8: C1 base id 0x800

  topology T {

    instance c1
    instance c2
    instance c3
    instance c4
    instance c5
    instance c6
    instance c7
    instance c8

    connections P {
      # Case 1: 2 ports that go to the same component instance
      # unmatched connections
      unmatched c1.pOut -> c1.pIn
      unmatched c2.pOut -> c1.pIn
      # implicit matched connections
      c1.pOut -> c1.pIn
      c2.pOut -> c1.pIn
      # explicit matched connection
      c1.pOut[1] -> c2.pIn[1]

      # Case 2: Have a connection out of A at some index i and
      # some connection at B at same index i but they go to different component instances 
      unmatched c3.pOut[0] -> c3.pIn[0]
      unmatched c4.pOut[0] -> c4.pIn[0]
      # implicit matched connections
      c3.pOut -> c4.pIn
      c4.pOut -> c3.pIn
      # explicit matched connections
      c3.pOut[1] -> c3.pIn[1]
      c4.pOut[1] -> c4.pIn[1]

      # Case 3: Have a connection out of A at index i and
      # some connection at B index at j not equal to i going to same component instance
      unmatched c5.pOut[0] -> c5.pIn[0]
      unmatched c6.pOut[1] -> c5.pIn[1]
      # implicit matched connection
      c6.pOut -> c5.pIn
      # explicit matched connection
      c5.pOut[2] -> c6.pIn[2]

      # Case 4: Have a connection out of A at index i and
      # some connection at B index at j not equal to i going to different component instances
      unmatched c7.pOut[0] -> c7.pIn[0]
      unmatched c8.pOut[1] -> c8.pIn[1]
      # implicit matched connections
      c7.pOut -> c8.pIn
      c8.pOut -> c7.pIn
      # explicit matched connections
      c7.pOut[2] -> c7.pIn[2]
      c8.pOut[3] -> c8.pIn[3]
      
    }
  }

}
