module M {

  instance $health: Svc.Health base id 0x100 {
    phase Phases.configComponents """
      // Test that ConfigObjects::M_health::pingEntries is in scope here
      (void) pingEntries;
    """
  }
  instance c1: C base id 0x200
  instance c2: C base id 0x300

  deployment topology Health {
    instance $health
    instance c1
    instance c2
    health connections instance $health
  }

}
