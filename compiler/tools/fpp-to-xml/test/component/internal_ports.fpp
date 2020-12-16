module M {

  active component InternalPorts {

    async input port p1: P

    internal port p2(a: U32)
    internal port p3(a: U32) priority 10
    internal port p4(a: U32) drop
    internal port p5(a: U32) priority 10 drop

  }

}
