port P

passive component C {
  sync input port pIn: P
  output port pOut: P
}

instance c1: C base id 0x100
instance c2: C base id 0x200
instance c3: C base id 0x300
instance c4: C base id 0x400

@ A simple topology
topology Simple1 {

  @ This specifier says that instance c1 is part of the topology
  instance c1
  @ This specifier says that instance c2 is part of the topology
  private instance c2

  @ This code specifies a connection graph C1
  connections C1 {
    c1.pOut -> c2.pIn
  }

  @ This code specifies a connection graph C2
  connections C2 {
    c2.pOut -> c1.pIn
  }

}

@ Another simple topology
topology Simple2 {
  import Simple1
  @ This specifier says that instance c3 is part of the topology
  instance c3
  @ This specifier says that instance c4 is part of the topology
  instance c4

  @ This code specifies a connection graph C1
  connections C3 {
    c3.pOut -> c4.pIn
  }

  @ This code specifies a connection graph C2
  connections C4 {
    c4.pOut -> c3.pIn
  }

}

@ A third Simple Topology
topology Simple3{
  import Simple2
}
