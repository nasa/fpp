module Fw {
  port Time
  port Log
}
module M {
  passive component Events {

  }
  passive component C {
    time get port timeGetOut
    event port eventOut
  }
  instance events: Events base id 0x100
  instance c: C base id 0x100
  topology T {
    instance events
    instance c
    event connections instance events { c }
  }
}
