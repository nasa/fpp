passive component C {
}
instance c: C base id 0x100
topology T {
  connections C {
    c.out -> c.in
  }
}
