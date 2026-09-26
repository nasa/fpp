module M {

  instance $health: Svc.Health base id 0x100 {
    phase Phases.configComponents "(void) pingEntries;"
  }
  instance unconnectedHealth: Svc.Health base id 0x400 {
    phase Phases.configComponents "(void) state;"
  }

  instance c1: C base id 0x200
  instance c2: C base id 0x300

  deployment topology Health {
    instance $health
    instance unconnectedHealth
    instance c1
    instance c2
    health connections instance $health { c1, c2 }
  }

}
