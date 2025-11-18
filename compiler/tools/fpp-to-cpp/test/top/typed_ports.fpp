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

  passive component PassiveSender {

    output port p1: [2] P1
    output port p2: [2] P2
    output port p3: [2] P3
    output port p4: [2] P4
    output port p5: [2] P5
    output port p6: [2] P6
    output port p7: [2] P7
    output port p8: [2] P8

  }

  passive component PassiveReceiver {

    sync input port p1: [2] P1
    sync input port p2: [2] P2
    sync input port p3: [2] P3
    sync input port p4: [2] P4
    sync input port p5: [2] P5
    sync input port p6: [2] P6
    sync input port p7: [2] P7
    sync input port p8: [2] P8

  }

  instance passiveSender: PassiveSender base id 0x100
  instance passiveReceiver: PassiveReceiver base id 0x200

  topology TypedPorts {

    instance passiveSender
    instance passiveReceiver

    connections C {

      passiveSender.p1[0] -> passiveReceiver.p1[1]
      passiveSender.p1[1] -> passiveReceiver.p1[0]

      passiveSender.p2[0] -> passiveReceiver.p2[1]
      passiveSender.p2[1] -> passiveReceiver.p2[0]

      passiveSender.p3[0] -> passiveReceiver.p3[0]
      passiveSender.p3[1] -> passiveReceiver.p3[1]

      passiveSender.p4 -> passiveReceiver.p4
      passiveSender.p4 -> passiveReceiver.p4

      passiveSender.p5 -> passiveReceiver.p5
      passiveSender.p5 -> passiveReceiver.p5

      passiveSender.p6 -> passiveReceiver.p6
      passiveSender.p6 -> passiveReceiver.p6

      passiveSender.p7 -> passiveReceiver.p7
      passiveSender.p7 -> passiveReceiver.p7

      passiveSender.p8 -> passiveReceiver.p8
      passiveSender.p8 -> passiveReceiver.p8

    }

  }

}
