module M {

  instance $health: Svc.Health base id 0x100

  instance c1: C base id 0x200
  instance c2: C base id 0x300

  topology Health {
    instance $health
    instance c1
    instance c2
    health connections instance $health
  }

}
