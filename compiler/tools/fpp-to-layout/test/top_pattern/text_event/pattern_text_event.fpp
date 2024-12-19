module Fw {
  type Time
  port Time($time: Fw.Time)
  port LogText
}
module M {
  passive component TextEvents {
    sync input port textEventIn: Fw.LogText
  }
  passive component C {
    time get port timeGetOut
    text event port textEventOut
  }
  instance textEvents: TextEvents base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x200
  topology T {
    instance textEvents
    instance c1
    instance c2
    text event connections instance textEvents
  }
}
