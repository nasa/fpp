
port P

passive component C1 { 

  output port pOut: P

}

passive component C2 { 

  sync input port pIn: P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x100
instance c3: C1 base id 0x200
instance c4: C2 base id 0x200
instance c5: C1 base id 0x300
instance c6: C2 base id 0x300

topology T1 { 

  instance c1
  instance c2

  connections C1 {
    c1.pOut -> c2.pIn
  }


  instance c3
  instance c4

  connections C2 {
    c3.pOut -> c4.pIn
  }

  instance c5
  instance c6

  connections C2 {
    c5.pOut -> c6.pIn
  }

}

topology T2 { 

  instance c1
  instance c2
  instance c3
  instance c4

  connections C3 {
    c1.pOut -> c2.pIn
    c3.pOut -> c4.pIn
  }

}
