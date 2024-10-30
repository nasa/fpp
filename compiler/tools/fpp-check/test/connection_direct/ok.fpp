port P

passive component C1 {
  output port pOut: [2] P
  output port serialOut: [2] serial
}
passive component C2 {
  sync input port pIn: P
  sync input port serialIn: serial
}

passive component C3 {
  output port pOut: [4] P
  sync input port pIn: [4] P
  match pOut with pIn
}

passive component C4 {
  output port pOut: [4] P
  sync input port pIn: [4] P
}

passive component C5 {
  output port pOut: [4] P
  sync input port pIn: [4] P
}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200
instance c3: C3 base id 0x300
instance c4: C4 base id 0x400
instance c5: C5 base id 0x500

topology T {
  instance c1
  instance c2
  instance c3
  instance c4
  instance c5

  connections C {

    c1.pOut -> c2.pIn
    c1.serialOut -> c2.pIn
    c1.pOut -> c2.serialIn
    c1.serialOut -> c2.serialIn
    
    # Matched connections to matched ports (c3.pOut, c3.pIn)
    c3.pOut -> c5.pIn
    c5.pOut -> c3.pIn

    # Unmatched connections to matched ports (c3.pOut, c3.pIn)
    unmatched c3.pOut[0] -> c4.pIn[0]
    unmatched c3.pOut -> c4.pIn

  }
}
