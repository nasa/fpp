module Fw {
  port Time
}

interface I {
}

passive component TimeSource {
  sync input port timeGetIn: Fw.Time
}

instance timeSource: TimeSource base id 0x100

# Sub satisfies the empty interface I, but it is a topology and not a
# component instance
topology Sub {
}

module template T(instance i: I) {
  topology Top {
    instance timeSource
    instance i
    # i is an explicit pattern target, so it must represent a component
    # instance
    time connections instance timeSource { i }
  }
}

expand T(instance Sub)
