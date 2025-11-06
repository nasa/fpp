port P

passive component C1 {

  output port pOut: [2] P

}

passive component C2 {

  sync input port pIn: P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200

topology A {
  instance c1
  instance c2

  port out = c1.pOut
  port in = c2.pIn
}

topology B {

  instance A

  port out = A.out

}

topology C {

  instance B

  connections P {
    B.out -> A.in
  }

}
