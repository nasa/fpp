module Fw {
  type Time
  port Time($time: Fw.Time)
}
module M {
  passive component Time {
    sync input port timeGetIn: Fw.Time
  }
  passive component C {
    time get port timeGetOut
  }
  instance $time: Time base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x300
  topology T {
    instance $time
    instance c1
    instance c2
    time connections instance $time
  }
}
