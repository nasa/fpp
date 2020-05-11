enum E { X = 1, Y = 2 }
struct S { x: E }
constant a = { x = E.X }
array A = [3] S default [ a, a, a ]
