port P

interface I {
    output port pOut: [2] P
}

passive component C1 {

  output port pOutDifferentName: [3] P

}

passive component C2 {

  sync input port pIn: P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200

topology A implements I {
  instance c1
  instance c2

  port pOut = c1.pOutDifferentName
  port b = c2.pIn
}

topology B {

  instance A

  connections P {
    A.pOut -> A.b
  }

}
