module TestComp {

  port Sched(
              context: U32
            )

  active component Device {

    async input port sched: Sched

    state machine Foo
    state machine instance foo1: Foo

  }

  instance dev1: FooBar \
      base id 0x100 \
      queue size 10

}
