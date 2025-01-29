module M {

  port P

  passive component A { 

    output port pOut: P

  }

  passive component B { 

    sync input port pIn: P

  }

  instance a1: A base id 0x100
  instance a2: A base id 0x200

  instance b: B base id 0x400

  topology S { 

    instance a1
    instance b

    connections C1 {
      a1.pOut -> b.pIn
    }

  }

  topology T {

    import S

    instance a2

    connections C2 {
      a2.pOut -> b.pIn
    }

  }

}
