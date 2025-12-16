module M {

  instance c1: C base id 0x100

  instance c2: C base id 0x200

  instance eventManager: EventManager base id 0x300

  instance noEvents: NoEvents base id 0x400

  topology Events {

    instance c1
    instance c2
    instance eventManager
    instance noEvents

    connections Events {
      c1.eventOut -> eventManager.eventIn
    }

  }

}
