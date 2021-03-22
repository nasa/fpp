port P1
port P2
passive component C1 {
  output port pOut: P1
}
passive component C2 {
  sync input port pIn: P2
}
instance c1: C1 base id 0x100
instance c2: C2 base id 0x200
topology T {
  connections C {
    c1.pOut -> c2.pIn
  }
}
