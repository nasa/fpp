
port P

passive component C1 { 

  output port pOut: P

}

passive component C2 { 

  sync input port pIn: P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200
instance c3: C1 base id 0x200
instance c4: C2 base id 0x200

topology T1 { 

  instance c1
  instance c2
  instance c3
  instance c4

  connections C1 {
    c1.pOut -> c2.pIn
  }

  connections C2 {
    c3.pOut -> c4.pIn
  }

}

topology T2 { 

  instance c1
  instance c2
  instance c3
  instance c4

  connections C {
    c1.pOut -> c2.pIn
    c3.pOut -> c4.pIn
  }

}
