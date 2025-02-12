module Fw {

  port Time

  port Tlm

}

passive component C {

  time get port timeGetOut

  telemetry port tlmOut

  telemetry T: U32

}

instance c1: C base id 0x100

instance c2: C base id 0x200

instance c3: C base id 0x300

instance c4: C base id 0x400
