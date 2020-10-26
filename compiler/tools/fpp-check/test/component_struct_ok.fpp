passive component C {
  struct S { x: U32 }
  struct T { x : S }
}

struct S { x: C.S }
