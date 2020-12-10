module M {

  active component C1 {

    async input port p1: P
    guarded input port p2: P
    sync input port p3: P
    output port p4: [10] P

  }

  passive component C2 {

    guarded input port p2: P
    sync input port p3: P
    output port p4: [10] P

  }

  passive component C3 {

  }

}
