locate type A at "A.fpp"
locate type G at "G.fpp"
locate constant s at "s.fpp"
locate type T at "T.fpp"

state machine M {

  type X = T

  action a: A
  guard g: G
  signal s: string size s

}
