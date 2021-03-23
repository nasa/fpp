port P
passive component C { 
  sync input port pIn: P
  output port pOut: P
}
instance c: C base id 0x100
topology B {
  import A 
  connections C {
    c.pOut -> c.pIn
  }
}
topology A {
  private instance c
}
