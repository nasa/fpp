module Fw {
  port Time
}

interface TimeUserI {
  time get port timeGetOut
}

passive component TimeSource {
  sync input port timeGetIn: Fw.Time
}

passive component User {
  time get port timeGetOut
}

instance timeSource: TimeSource base id 0x100
instance u: User base id 0x200
instance v: User base id 0x300

module template T(instance i: TimeUserI) {
  topology Top {
    instance timeSource
    instance i
    instance v
    time connections instance timeSource { i }
  }
}

expand T(instance u)
