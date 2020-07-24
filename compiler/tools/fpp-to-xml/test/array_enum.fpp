module M {

  enum ArrayEnumE1 {
    X = 1 @< Example comment
    Y = 2
    Z = 9
  }

}

enum ArrayEnumE2 {
  A = 10
  B = 20
  C = 30
  D = 40
}

module N {
  array ArrayEnum1 = [2] M.ArrayEnumE1 @< Array with enum argument
}

array ArrayEnum2 = [5] ArrayEnumE2
