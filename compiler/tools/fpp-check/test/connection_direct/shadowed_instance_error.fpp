port P

passive component C1 {

  output port p: P

}

passive component C2 {

  sync input port p: .P

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200

module M {

  port P(x: U32)

  passive component C2 {

    sync input port p: P

  }

  instance c2: C2 base id 0x300

  topology T {

    instance c1
    instance c2

    connections C {
      c1.p -> c2.p
    }

  }

}
