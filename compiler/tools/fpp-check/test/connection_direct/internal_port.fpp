port P
active component C {
  internal port pInternal
  sync input port pIn: P
}
instance c: C base id 0x100 \
  queue size 10 \
  stack size 1024 \
  priority 10
topology T {
  connections C {
    c.pInternal -> c.pIn
  }
}
