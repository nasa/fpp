# Uses inside an expanded template body that resolve to bound template parameters
# The array size pins the constant argument to 3
# The array default pins the type argument to Elt

port P

interface I {
  sync input port pIn: P
}

passive component C {
  import I
}

instance i1: C base id 0x100

struct Elt { x: U32 }

module template T(constant n: U32, type Ty, instance i: I) {
  array A_use = [n] Ty default [{ x = 1 }, { x = 2 }, { x = 3 }]
  topology Top_use {
    instance i
  }
}

module M {
  expand T(constant 3, type Elt, instance i1)
}
