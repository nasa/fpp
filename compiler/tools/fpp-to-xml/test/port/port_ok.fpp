# Due to a bug in the F Prime autocoder, all ports must be
# enclosed in modules

locate type M.E at "E.fpp"
locate type M.A at "A.fpp"
locate type M.S at "S.fpp"

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
  ) -> F32

  port PortOK4(
    ref a: A
    ref s: S
    ref e: E
  )

}
