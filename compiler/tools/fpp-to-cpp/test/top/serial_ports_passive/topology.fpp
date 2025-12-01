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

    connections TypedToSerial {

      sender.pTyped[0] -> receiver.pSerialSync[0]
      sender.pTyped[1] -> receiver.pSerialSync[1]

      sender.pTyped[2] -> receiver.pSerialGuarded[0]
      sender.pTyped[3] -> receiver.pSerialGuarded[1]

    }

  }

}
