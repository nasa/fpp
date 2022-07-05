module M {
  constant length = 3

  @ An array of primitives
  array Primitive1 = [length] U32

  @ An array of primitives with specified default value and format string
  array Primitive2 = [length+2] F32 default 1 format "{.03f}"
}

@ An array of arrays
array PrimitiveArray = [5] M.Primitive2
