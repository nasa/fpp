module M {

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
