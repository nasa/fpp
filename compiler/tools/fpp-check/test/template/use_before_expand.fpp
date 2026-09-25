# Negative companion of use_before_expand_ok: the pre-expand use of the
# expansion-contributed constant k is checked against the bound value 4,
# so the default value of size 3 no longer fits.

module template T(constant n: U32) {
  constant k = n
}

array UsesConstant = [k] U32 default [1, 2, 3]

expand T(constant 4)
