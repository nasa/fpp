port P

active component C {

  async input port p: P

}

instance c: C base id 0x100 \
  at "C.hpp" \
  queue size 10 \
  stack size 10 * 1024 \
  priority 3 \
  cpu 0
