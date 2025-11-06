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

  port a = c1.pOut
  port b = c2.pIn
}

topology B {

  instance A

  connections P {
    A.a -> A.a
  }

}
