port P
passive component C {
  output port pOut: P
  sync input port pIn: P
}
instance c: C base id 0x100
topology T {
  connections C {
    c.pOut[1] -> c.pIn
  }
}
