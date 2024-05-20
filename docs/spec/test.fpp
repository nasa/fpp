module TestComp {

  state machine Foo

  port Sched(
              context: U32
            )

  active component Device {

    async input port sched: Sched

    state machine Bar

    state machine instance foo1: Foo
    state machine instance foo2: Foo
    state machine instance bar: Boo

  }

}
