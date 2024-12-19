module Fw {
  port PrmGet
  port PrmSet
}
module M {
  passive component Parameters {
    sync input port prmGetIn: Fw.PrmGet
    sync input port prmSetIn: Fw.PrmSet
  }
  passive component C {
    param get port prmGetOut
    param set port prmSetOut
  }
  instance parameters: Parameters base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x300
  topology T {
    instance parameters
    instance c1
    instance c2
    param connections instance parameters
  }
}
