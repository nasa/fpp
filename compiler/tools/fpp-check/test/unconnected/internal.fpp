module M {

  active component C {

    internal port p

  }

  instance c: C base id 0x100 queue size 10 stack size 1024 priority 1

  topology T1 {

    instance c

  }

}
