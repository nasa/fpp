# An explicit unsigned primitive integer size-prefix type, and an alias to one
type MyU8 = U8

vector V = [U16 size 3] U8
vector W = [MyU8 size 3] U8
