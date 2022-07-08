module M {
  enum E1 {
    X = 1 @< Example comment
    Y = 2
    Z = 9
  }
}

@ An enum with specified default value
enum E2 {
  A = 10
  B = 20
  C = 30
  D = 40
} default C

array Enum1 = [2] M.E1 default [ M.E1.X, M.E1.Y ] @< Array with enum argument
array Enum2 = [5] E2
