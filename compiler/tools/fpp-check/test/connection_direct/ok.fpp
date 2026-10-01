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

instance c3_s: C3 base id 0x600
instance c4_s: C4 base id 0x700
instance c5_s: C5 base id 0x800

topology S {
  instance c3_s
  instance c4_s
  instance c5_s

  port c3_pIn = c3_s.pIn
  port c3_pOut = c3_s.pOut
  port c4_pIn = c4_s.pIn
  port c5_pIn = c5_s.pIn
  port c5_pOut = c5_s.pOut
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
  instance S

  connections C {

    c1.pOut -> c2.pIn
    c1.serialOut -> c2.pIn
    c1.pOut -> c2.serialIn
    c1.serialOut -> c2.serialIn
    
    # Matched connections to matched component instance ports
    c3.pOut -> c5.pIn
    c5.pOut -> c3.pIn

    # Matched connections to matched topology ports
    S.c3_pOut -> S.c5_pIn
    S.c5_pOut -> S.c3_pIn

    # Unmatched connections to matched component instance ports
    unmatched c3.pOut[0] -> c4.pIn[0]
    unmatched c3.pOut -> c4.pIn

    # Unmatched connections to matched topology ports
    unmatched S.c3_pOut[0] -> S.c4_pIn[0]
    unmatched S.c3_pOut -> S.c4_pIn

  }
}
