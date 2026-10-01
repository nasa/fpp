# A definition contributed by an expansion may be used earlier in the
# model than the expand specifier that creates it. Both uses below
# deliberately precede the expand specifier: do not reorder them.

module template T(constant n: U32, type P) {
  constant k = n
  type Alias = P
}

array Triple = [3] U32

# Type checks only if Alias is an array type of size 3: pins the type arg
array UsesType = [1] Alias default [ [1, 2, 3] ]

# Type checks only if k is 3: pins the constant arg
array UsesConstant = [k] U32 default [1, 2, 3]

expand T(constant 3, type Triple)
