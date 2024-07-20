module Fw {
  port BufferSend
}

module Svc {
  port Sched
}

module Utils {

  @ A component for compressing data
  active component DataCompressor {

    @ Uncompressed input data
    async input port bufferSendIn: Fw.BufferSend

    @ Compressed output data
    output port bufferSendOut: Fw.BufferSend

  }

}

module M {

  @ A component with state machines
  active component StateMachines {

    @ Sched in port
    async input port schedIn: Svc.Sched

    @ State machine S1
    state machine S1

    @ State machine S2
    state machine S2 {
        initial enter IDLE
        state IDLE
    }

    @ State machine instance s1
    state machine instance s1: S1

    @ State machine instance s2
    state machine instance s2: S2

  }

}

module FSW {

  module Default {
    @ Default queue size
    constant queueSize = 10
    @ Default stack size
    constant stackSize = 10 * 1024
  }

  @ Data compressor instance
  instance dataCompressor: Utils.DataCompressor base id 0x100 \
    queue size Default.queueSize \
    stack size Default.stackSize \
    priority 30

  @ State machines instance
  instance stateMachines: M.StateMachines base id 0x200 \
    queue size Default.queueSize \
    stack size Default.stackSize \
    priority 40

}
