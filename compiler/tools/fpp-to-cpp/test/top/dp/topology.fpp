module M {

  instance c1: C base id 0x100 \
    queue size 100

  instance c2: C base id 0x200 \
    queue size 100

  instance dpManager: DpManager base id 0x300

  instance noDp: NoDp base id 0x400

  topology Dp {

    instance c1
    instance c2
    instance dpManager
    instance noDp

    connections Dp {
      c1.productGetOut -> dpManager.productGetIn
      c1.productSendOut -> dpManager.productSendIn
    }

  }

}
