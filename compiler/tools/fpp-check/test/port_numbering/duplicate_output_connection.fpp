port P

passive component C1 {

  output port pOut: [2] P

}

passive component C2 {

  sync input port pIn: P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200

topology T {

  instance c1
  instance c2

  connections P {
    c1.pOut[0] -> c2.pIn
    c1.pOut[0] -> c2.pIn
  }

}
