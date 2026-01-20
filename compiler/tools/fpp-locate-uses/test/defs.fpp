# Placeholders for core F Prime ports
# used in special port instances
module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
  port DpRequest
  port DpResponse
  port DpSend
  port Log
  port LogText
  port PrmGet
  port PrmSet
  port Time
  port Tlm
}

dictionary array A = [3] U32
constant a = 0
enum E { X, Y }
enum Phases { setup, teardown }
struct S { x: U32 }
type T
state machine SM
constant t = {seconds=10,useconds=20}

module M {
  array A = [3] U32
  dictionary constant a = 0
  enum E { X, Y }
  struct S { x: U32 }
  type T
  state machine SM
} 

port P

active component C1 {
  async input port pIn: P
  array A = [3] U32
  constant a = 0
  enum E { X, Y }
  dictionary struct S { x: U32 }
  type T
  state machine SM
}

instance c11: C1 \
  base id 0x100 \
  queue size 10 \
  stack size 1024 \
  priority 10

topology T1 {

}

module M {

  passive component C1 {
    array A = [3] U32
    constant a = 0
    dictionary enum E { X, Y }
    struct S { x: U32 }
    type T
    state machine SM
  }

  instance c11: C1 base id 0x100

  topology T1 {

  }

}

constant base_id_def = 0x200
constant queue_size_def = 10
constant stack_size_def = 10
constant priority_def = 10
constant cpu_def = 0
constant record_id = 0
dictionary array RecordType = [3] U32
dictionary constant container_id = 0
constant container_priority = 0
constant product_recv_priority = 0

type ActionType
type GuardType
type SignalType
constant SignalConstant = 10

constant tlm_packet_id = 0
constant tlm_packet_group = 0
