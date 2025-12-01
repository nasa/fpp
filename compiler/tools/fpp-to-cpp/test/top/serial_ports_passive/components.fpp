module SerialPortsPassive {

  array A = [3] U32
  enum E { A, B }
  struct S { x: U32 }

  port PTyped(
      x1: U32,
      x2: F32,
      x3: bool,
      x4: string,
      x5: A,
      x6: E,
      x7: S
  )

  passive component Sender {

    output port pTyped: [2] PTyped
    output port pSerial: [4] serial

  }

  passive component Receiver {

    sync input port pTypedSync: [2] PTyped
    guarded input port pTypedGuarded: [2] PTyped
    sync input port pSerialSync: [2] serial
    guarded input port pSerialGuarded: [2] serial

  }

}
