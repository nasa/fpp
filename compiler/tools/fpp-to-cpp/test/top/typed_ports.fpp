module M {

  port P1(x: U32)
  port P2(x: U32) -> U32

  passive component PassiveSender {

    output port p1: [2] P1
    output port p2: [2] P2

  }

  passive component PassiveReceiver {

    sync input port p1: [2] P1
    sync input port p2: [2] P2

  }

  instance passiveSender: PassiveSender base id 0x100
  instance passiveReceiver: PassiveReceiver base id 0x200

  topology TypedPorts {

    instance passiveSender
    instance passiveReceiver

    connections C {

      passiveSender.p1 -> passiveReceiver.p1
      passiveSender.p1 -> passiveReceiver.p1

      passiveSender.p2[0] -> passiveReceiver.p2[1]
      passiveSender.p2[1] -> passiveReceiver.p2[0]

    }

  }

}
