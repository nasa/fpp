port P

passive component C1 {

  output port p: P

}

instance c1: C1 base id 0x100

module M {

  port P(x: U32)

  passive component C2 {

    sync input port p: P

  }

  instance c2: C2 base id 0x200

}

topology T {

  instance c1
  instance M.c2

  connections C {
    c1.p -> M.c2.p
  }

}
