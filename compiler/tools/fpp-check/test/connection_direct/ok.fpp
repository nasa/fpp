port P
port P2

passive component C1 {
  output port pOut: [2] P
  output port serialOut: [2] serial
}
passive component C2 {
  sync input port pIn: P
  sync input port serialIn: serial
}

passive component C3 {
  output port pOut: [5] P2
  sync input port pIn: [5] P2
  match pOut with pIn
}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200
instance c3: C3 base id 0x300
instance c4: C3 base id 0x400

topology T {
  instance c1
  instance c2
  instance c3
  instance c4
  connections C {
    c1.pOut -> c2.pIn
    c1.serialOut -> c2.pIn
    c1.pOut -> c2.serialIn
    c1.serialOut -> c2.serialIn

    unmatched c3.pOut[0] -> c4.pIn[0]
    unmatched c3.pOut -> c4.pIn
  }
}
