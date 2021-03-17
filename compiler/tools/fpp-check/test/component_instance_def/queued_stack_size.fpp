port P

queued component C {

  async input port p: P

}

instance c: C base id 0x100 \
  queue size 10 \
  stack size 10 * 1024
