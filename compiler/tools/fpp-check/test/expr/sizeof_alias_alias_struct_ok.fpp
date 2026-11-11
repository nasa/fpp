# The struct is reached through two aliases and is not finalized
# before the sizeof expression
constant c = sizeof(T1)

type T1 = T2

type T2 = S

struct S { x: U32, y: Inner } default { x = 1 }

struct Inner { a: U8 } default { a = 2 }
