port P1
port P2

interface I {

  output port p1Out: [2] P1
  sync input port p1In: [2] P1

  output port p2Out: P2

}

passive component C1 {

  output port p1Out: [2] P1
  sync input port p1In: [2] P1

  match p1In with p1Out

  output port p2Out: P2

}

passive component C2 {

  output port p1Out: P1
  sync input port p1In: P1

  sync input port p2In: P2

}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x200

module template T(instance i: I) {

  topology Top {

    instance i
    instance c2

    connections P1 {
      i.p1Out -> c2.p1In
      c2.p1Out -> i.p1In
    }

    connections P2 {
      i.p2Out -> c2.p2In
    }

  }

}

expand T(instance c1)
