module M {

  port P

  passive component C1 { 

    output port pOut: P

  }

  passive component C2 { 

    sync input port pIn: P

  }

  instance c1: C1 base id 0x100
  instance c2: C2 base id 0x200

  topology T { 

    instance c1
    instance c2

    connections C {
      c1.pOut -> c2.pIn
    }

  }

}
