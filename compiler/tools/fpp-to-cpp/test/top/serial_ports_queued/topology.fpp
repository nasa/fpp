module SerialPortsQueued {

  instance sender: Sender base id 0x100
  instance receiver: Receiver base id 0x200 \
    queue size 10

  topology SerialPortsQueued {

    instance sender
    instance receiver

    connections SerialToTyped {

      sender.pSerial[0] -> receiver.pTypedSync[0]
      sender.pSerial[1] -> receiver.pTypedSync[1]

      sender.pSerial[2] -> receiver.pTypedAsync[0]
      sender.pSerial[3] -> receiver.pTypedAsync[1]

    }

    connections TypedToSerial {

      sender.pTyped[0] -> receiver.pSerialSync[0]
      sender.pTyped[1] -> receiver.pSerialSync[1]

      sender.pTyped[2] -> receiver.pSerialAsync[0]
      sender.pTyped[3] -> receiver.pSerialAsync[1]

    }

    connections SerialToSerial {

      sender.pSerial[4] -> receiver.pSerialSync[2]
      sender.pSerial[5] -> receiver.pSerialSync[3]

      sender.pSerial[6] -> receiver.pSerialAsync[2]
      sender.pSerial[7] -> receiver.pSerialAsync[3]

    }

  }

}
