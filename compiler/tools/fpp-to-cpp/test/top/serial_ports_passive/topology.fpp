module SerialPortsPassive {

  instance sender: Sender base id 0x100
  instance receiver: Receiver base id 0x200

  topology SerialPortsPassive {

    instance sender
    instance receiver

    connections SerialToTyped {

      sender.pSerial[0] -> receiver.pTypedSync[0]
      sender.pSerial[1] -> receiver.pTypedSync[1]

      sender.pSerial[2] -> receiver.pTypedGuarded[0]
      sender.pSerial[3] -> receiver.pTypedGuarded[1]

    }

  }

}
