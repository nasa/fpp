module template T(type Ty) {

  array A = [3] Ty

  struct S { x: Ty }

}

module M {
  expand T(type U32)
}
