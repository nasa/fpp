module M {

  enum Explicit {
    X = 1 @< Member X
    Y = 2
  }

}
module N {
array ABC = [2] M.Explicit
}