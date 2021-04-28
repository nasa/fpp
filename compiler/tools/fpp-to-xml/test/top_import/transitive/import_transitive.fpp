module M {

  port P

  passive component A { 

    output port pOut: P

  }

  passive component B { 

    sync input port pIn: P

  }

  instance a: A base id 0x100
  instance b: B base id 0x200

  topology A { 

    instance a
    instance b

    connections C {
      a.pOut -> b.pIn
    }

  }

  topology B { import A }

  topology C { import A }

  topology T {

    import B
    import C

  }

}
