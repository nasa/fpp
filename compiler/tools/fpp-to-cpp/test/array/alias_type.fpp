type AT = U32

@ An array of abstract type
array AliasType = [3] AT default [0, 2, 3]

type ATF32 = F32

@ An array of aliased F32 with a format string
array AliasTypeFormat = [3] ATF32 format "{.3e}"
