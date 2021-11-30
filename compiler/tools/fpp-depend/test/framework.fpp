port P

passive component C1 {
  guarded input port p: P
}

module M {
  active component C2 {
    async input port p: P
  }
}
