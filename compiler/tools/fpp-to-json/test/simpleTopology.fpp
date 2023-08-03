port P

passive component C {
  sync input port pIn: P
  output port pOut: P
}

instance c1: C base id 0x100
instance c2: C base id 0x200

@ A simple topology
topology Simple {

  @ This specifier says that instance c1 is part of the topology
  instance c1
  @ This specifier says that instance c2 is part of the topology
  instance c2

  @ This code specifies a connection graph C1
  connections C1 {
    c1.pOut -> c2.pIn
  }

  @ This code specifies a connection graph C2
  connections C2 {
    c2.pOut -> c1.pIn
  }

}
