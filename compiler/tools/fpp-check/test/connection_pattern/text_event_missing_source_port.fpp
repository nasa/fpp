module Fw {
  port Time
  port LogText
}
module M {
  passive component TextEvents {

  }
  passive component C {
    time get port timeGetOut
    text event port textEventOut
  }
  instance textEvents: TextEvents base id 0x100
  instance c: C base id 0x100
  topology T {
    instance textEvents
    instance c
    text event connections instance textEvents { c }
  }
}
