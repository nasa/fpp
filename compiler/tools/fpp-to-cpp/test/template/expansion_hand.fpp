port P

struct Elt { x: U32 }

module M {

  @ Array from a template
  array A = [3] Elt

  @ Struct from a template
  struct S {
    e: Elt
    a: A
  }

  @ Port from a template
  port Q(s: S)

  @ Component from a template
  passive component C {
    sync input port pIn: P
    output port qOut: Q
    constant k = 3 + 1
  }

}
