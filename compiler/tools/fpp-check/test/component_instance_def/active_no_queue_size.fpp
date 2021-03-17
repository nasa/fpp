port P

active component C {

  async input port p: P

}

instance c: C base id 0x100 \
  stack size 10 * 1024 \
  priority 10

