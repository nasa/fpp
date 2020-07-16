module M {

  enum EnumModule {
    X = 1 @< Example comment
    Y = 2
    Z = 9
  }

}

enum EnumGlobal {
  A = 10
  B = 20
  C = 30
  D = 40
}

module N {
array ABC = [2] M.EnumModule
}

array DEF = [5] EnumGlobal