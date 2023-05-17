module M {
  struct Modules1 {
    x: U32
    y: F32
  }

  struct Modules2 {
    x: M.Modules1
  }
}

struct Modules3 {
  x: M.Modules2
  arr: [3] M.Modules2
}

struct Modules4 {
  arr1: [3] M.Modules2
  arr2: [6] M.Modules2
}
