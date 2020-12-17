module M {

  active component Commands {

    async input port p: P

    command reg port cmdRegOut
    command recv port cmdIn
    command resp port cmdResponseOut

    @ Command C1
    async command C1 
    async command C2 priority 10
    async command C3 drop
    async command C4 priority 10 drop

    @ Command C5
    sync command C5(a: U32)

    @ Command C6
    guarded command C6(a: F32)

  }

}
