module TestComp {

  port Sched(
              context: U32
            )

  active component Device {

    async input port sched: Sched

  }

}
