module Utils {

  @ A component for compressing data
  active component DataCompressor {

    @ Uncompressed input data
    async input port bufferSendIn: Fw.BufferSend

    @ Compressed output data
    output port bufferSendOut: Fw.BufferSend

    state machine LedSm
    state machine instance led1: LedSm
    state machine instance led2: LedSm

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

}

module Fw {
  port BufferSend
}
