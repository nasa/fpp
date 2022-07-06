module M {
  constant length = 3
  constant default_val = [ 1, 2, 3 ]

  array PrimitiveBool = [length] bool

  array PrimitiveU8 = [length] U8

  array PrimitiveU16 = [length] U16 default default_val

  @ An array of I32 with format string
  array PrimitiveI32 = [length] I32 format "{o}"

  @ An array of I64 with format string
  array PrimitiveI64 = [length] I64 format "{x}"

  @ An array of F32 with format string
  array PrimitiveF32f = [length] F32 format "{.1f}"

  @ An array of F32 with default value and format string
  array PrimitiveF32e = [length] F32 default 1 format "{.3e}"

  @ An array of F64 with default value and format string
  array PrimitiveF64 = [length+2] F64 default [ 1, 2, 3, 4, 5 ] format "{.5g}"
}

@ An array of arrays
array PrimitiveArray = [5] M.PrimitiveF64
