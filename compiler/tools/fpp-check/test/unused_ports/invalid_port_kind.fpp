active component C {
  internal port pInternal
}

instance c: C base id 0x100 \
  queue size 10 \
  stack size 1024 \
  priority 10

topology T {
  unused {
    c.pInternal
  }
}
