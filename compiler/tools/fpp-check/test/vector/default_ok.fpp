type FwSizeStoreType = U16

vector V = [size 4] U8 default [ 1, 2 ]

passive component C {
  vector V = [size 4] U8 default [ 1, 2 ]
}

module M {
  vector V = [size 4] U8 default [ 1, 2 ]
}
