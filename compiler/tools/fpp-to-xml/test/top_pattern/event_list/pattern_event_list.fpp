module Fw {
  type Time
  port Time($time: Fw.Time)
  port Log
}
module M {
  passive component Events {
    sync input port eventIn: Fw.Log
  }
  passive component C {
    time get port timeGetOut
    event port eventOut
  }
  instance events: Events base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x300
  topology T {
    instance events
    instance c1
    instance c2
    event connections instance events { c1 }
  }
}
