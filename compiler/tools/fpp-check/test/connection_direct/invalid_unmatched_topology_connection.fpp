port P

passive component C1 {

  output port pOut: P

}

passive component C2 {

  sync input port pIn: P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200

topology S {

  instance c2

  port c2_pIn = c2.pIn

}

topology T {

  instance c1
  instance S

  connections C {

    unmatched c1.pOut -> S.c2_pIn

  }

}
