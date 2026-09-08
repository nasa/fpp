port P

passive component CMatch {

  sync input port pIn: [2] P
  output port pOut: [2] P
  match pIn with pOut

}

passive component C2 {

  sync input port pIn: P
  output port pOut: P

}

instance cm: CMatch base id 0x100
instance c2: C2 base id 0x200

topology Top {

  instance cm
  instance c2

  connections P {
    cm.pOut -> c2.pIn
  }

}
