module M {
  @ An array of primitives
  array Primitive1 = [3] U32

  @ An array of primitives with specified default value and format string
  array Primitive2 = [5] F32 default 1 format "{.03f}"
}

@ An array of arrays
array PrimitiveArray = [5] M.Primitive2
