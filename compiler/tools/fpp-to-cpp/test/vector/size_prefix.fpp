@ An alias of an unsigned primitive integer type
type MyU8 = U8

@ A vector with an explicit U16 size-prefix type
vector ExplicitPrefix = [U16 size 3] U32

@ A vector with an alias size-prefix type
vector AliasPrefix = [MyU8 size 3] U32
