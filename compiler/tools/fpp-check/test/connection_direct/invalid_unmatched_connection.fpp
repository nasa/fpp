port P1
port P2

passive component C1 {

  output port p1Out: [5] P1
  sync input port p1In: [5] P1

  output port p2Out: P2
  sync input port p2In: P2

}

instance c1: C1 base id 0x100
instance c2: C1 base id 0x200

topology T {

  instance c1
  instance c2

  connections P1 {
    unmatched c1.p1Out[0] -> c2.p1In[0]
    unmatched c1.p1Out -> c2.p1In
  }

  connections P2 {
    c1.p2Out -> c2.p2In
  }

}
