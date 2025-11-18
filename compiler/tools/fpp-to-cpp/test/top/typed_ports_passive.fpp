module M {

  array A = [3] U32
  enum E { A, B }
  struct S { x: U32 }

  port P1(
      x1: U32,
      x2: F32,
      x3: bool,
      x4: string,
      x5: A,
      x6: E,
      x7: S
  )
  port P2(x: U32) -> U32
  port P3(x: F32) -> F32
  port P4(x: bool) -> bool
  port P5(x: string) -> string
  port P6(x: A) -> A
  port P7(x: E) -> E
  port P8(x: S) -> S

  passive component TypedPortsPassiveSender {

    output port p1: [2] P1
    output port p2: [2] P2
    output port p3: [2] P3
    output port p4: [2] P4
    output port p5: [2] P5
    output port p6: [2] P6
    output port p7: [2] P7
    output port p8: [2] P8

  }

  passive component TypedPortsPassiveReceiver {

    sync input port p1: [2] P1
    sync input port p2: [2] P2
    sync input port p3: [2] P3
    sync input port p4: [2] P4
    guarded input port p5: [2] P5
    guarded input port p6: [2] P6
    guarded input port p7: [2] P7
    guarded input port p8: [2] P8

  }

  instance sender: TypedPortsPassiveSender base id 0x100
  instance receiver: TypedPortsPassiveReceiver base id 0x200

  topology TypedPortsPassive {

    instance sender
    instance receiver

    connections C {

      sender.p1[0] -> receiver.p1[1]
      sender.p1[1] -> receiver.p1[0]

      sender.p2[0] -> receiver.p2[1]
      sender.p2[1] -> receiver.p2[0]

      sender.p3[0] -> receiver.p3[0]
      sender.p3[1] -> receiver.p3[1]

      sender.p4 -> receiver.p4
      sender.p4 -> receiver.p4

      sender.p5 -> receiver.p5
      sender.p5 -> receiver.p5

      sender.p6 -> receiver.p6
      sender.p6 -> receiver.p6

      sender.p7 -> receiver.p7
      sender.p7 -> receiver.p7

      sender.p8 -> receiver.p8
      sender.p8 -> receiver.p8

    }

  }

}
