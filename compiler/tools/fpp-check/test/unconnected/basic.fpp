module M {

  port P1
  port P2

  passive component C {

    sync input port p1In: P1
    sync input port p2In: P2
    output port p1Out: P1
    output port p2Out: P2

  }

  instance c: C base id 0x100

  topology T1 {

    instance c
    connections C {
      c.p1Out -> c.p1In
    }

  }

  topology T2 {

    instance c
    connections C {
      c.p1Out -> c.p1In
    }

  }

}
