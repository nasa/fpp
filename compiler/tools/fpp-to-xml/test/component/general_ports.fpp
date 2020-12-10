module M {

  active component GeneralPorts1 {

    async input port p1: P
    guarded input port p2: P
    sync input port p3: P
    output port p4: [10] P

  }

  passive component GeneralPorts2 {

    guarded input port p2: P
    sync input port p3: P
    output port p4: [10] P

  }

  passive component GeneralPorts3 {

  }

}
