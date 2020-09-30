# Due to a bug in the F Prime autocoder, all ports must be
# enclosed in modules

locate type M.Explicit at "enum_explicit.fpp"
locate type M1.ArrayOK1 at "array_ok.fpp"
locate type StructOK1 at "struct_ok.fpp"

module M {

  port PortOK1

  @ Top-level comment
  port PortOK2(
    @ Comment for parameter a
    a: U32
  )

  port PortOK3(
    a: U32
    ref b: string size 40
  ) -> string size 40

  port PortOK4(
    ref a: M1.ArrayOK1
    ref s: StructOK1
    ref e: Explicit
  )

}
