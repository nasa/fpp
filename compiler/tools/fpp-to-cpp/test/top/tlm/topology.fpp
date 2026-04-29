module M {

  instance c1: C base id 0x100

  instance c2: C base id 0x200

  instance tlmManager: TlmManager base id 0x300

  instance noTlm: NoTlm base id 0x400

  topology Tlm {

    instance c1
    instance c2
    instance tlmManager
    instance noTlm

    connections Tlm {
      c1.tlmOut -> tlmManager.tlmIn
    }

  }

}
