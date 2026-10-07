constant a = 0

module template T(constant a: bool) {
  array A = [3] U32 default [ a, a, a ]
}

expand T(constant false)
