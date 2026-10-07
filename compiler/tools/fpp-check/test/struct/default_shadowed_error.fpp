constant s = { x = 1 }
module M {
  constant s = { x = false}
  struct S { x: U32 } default s
}
