port P

struct Elt { x: U32 }

module template T(constant n: U32, type Ty) {

  @ Array from a template
  array A = [n] Ty

  @ Struct from a template
  struct S {
    e: Ty
    a: A
  }

  @ Port from a template
  port Q(s: S)

  @ Component from a template
  passive component C {
    sync input port pIn: P
    output port qOut: Q
    constant k = n + 1
  }

}

module M {
  expand T(constant 3, type Elt)
}
