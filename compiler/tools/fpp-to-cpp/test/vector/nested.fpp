@ A vector of at most two U8 elements
vector Inner = [size 2] U8

@ A vector of vectors with a default value
vector Outer = [size 3] Inner default [ [ 1, 2 ], [ 3, 4 ] ]

@ An array of vectors
array ArrayOfVector = [2] Inner
