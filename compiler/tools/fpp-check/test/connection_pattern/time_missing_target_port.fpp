module Fw {
  port Time
}
module M {
  passive component Time {
    sync input port timeGetIn: Fw.Time
  }
  passive component C {

  }
  instance $time: Time base id 0x100
  instance c: C base id 0x100
  topology T {
    instance $time
    instance c
    time connections instance $time { c }
  }
}
