state machine SM {
  struct S { x: U32 }
  struct T { x : S }
  state ST
  initial enter ST
}

struct S { x: SM.S }
