module Fw {
  port PrmGet
  port PrmSet
}
module M {
  passive component Parameters {
    sync input port prmGetIn: Fw.PrmGet
    sync input port prmGetIn1: Fw.PrmGet
    sync input port prmSetIn: Fw.PrmSet
  }
  passive component C {
    param get port prmGetOut
    param set port prmSetOut
  }
  instance parameters: Parameters base id 0x100
  instance c: C base id 0x100
  topology T {
    instance parameters
    instance c
    param connections instance parameters { c }
  }
}
