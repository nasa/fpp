@ Port P1
port P1()

@ Port P2
port P2(a : U32, b : F64)

@ Port P3
port P3(
  @ Argument a
  a : U32
  @ Argument b
  b : F64
)

@ Port P4
port P4(ref a : U32, ref b : F64)

@ Port P5
port P5() -> I32

@ Port P6
port P6(a : U32, b : F64) -> I32

@ Port P7
port P7(
  a : U32 @< Argument a
  b : F64 @< Argument b
) -> I32

@ Port P8
port P8(ref a : U32, ref b : F64) -> I32

