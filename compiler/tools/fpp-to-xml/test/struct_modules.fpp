module M {
  struct StructModules1 {
    x: U32
  }
  struct StructModules2 {
    x: M.StructModules1
  }
}

struct StructModules3 {
  x: M.StructModules1
}
