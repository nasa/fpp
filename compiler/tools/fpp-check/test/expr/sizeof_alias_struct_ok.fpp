# The aliased struct is not finalized before the sizeof expression
constant c = sizeof(T)

type T = S

struct S { x: U32 } default { x = 1 }
