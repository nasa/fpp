module M {

  @ Component GeneralPorts2
  active component GeneralPorts1 {

    @ Port p1
    async input port p1: P
    guarded input port p2: P
    sync input port p3: P
    output port p4: [10] P


    async input port p5: serial
    guarded input port p6: serial
    sync input port p7: serial
    output port p8: [10] serial

  }

  @ Component GeneralPorts1
  passive component GeneralPorts2 {

    @ Port p1
    guarded input port p1: P
    sync input port p2: P
    output port p3: [10] P

    guarded input port p4: serial
    sync input port p5: serial
    output port p6: [10] serial

  }

}
