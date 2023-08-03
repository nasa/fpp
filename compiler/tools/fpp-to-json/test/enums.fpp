enum Decision {
  YES
  NO
  MAYBE
}
constant initialState = Decision.MAYBE

enum E { A = 1, B = 2, C = 3 }
constant c = E.A + 1

enum Small : U8 { A, B, C } default B
