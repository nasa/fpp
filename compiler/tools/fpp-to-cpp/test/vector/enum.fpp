module M {
  enum E1 {
    X = 1 @< Example comment
    Y = 2
    Z = 9
  }
}

vector Enum1 = [size 2] M.E1 default [ M.E1.X, M.E1.Y ] @< Vector with enum element
vector Enum2 = [size 5] M.E1
