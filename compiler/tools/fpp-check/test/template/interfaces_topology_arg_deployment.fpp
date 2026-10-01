interface I {
}

deployment topology Dep {
}

module template T(instance i: I) {
  topology Outer {
    instance i
  }
}

expand T(instance Dep)
