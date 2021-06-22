module M {

  port P

  enum Phases {
    configConstants
    configObjects
    instances
    initComponents
    configComponents
  }

  active component C1 {

    async input port p: P

  }

  passive component C2 {

    output port p: P

  }

  instance c1: C1 base id 0x100 queue size 10 stack size 1024 priority 1
  instance c2: C2 base id 0x200

  init c1 phase Phases.configConstants """
  enum {
    X = 0,
    Y = 1
  };
  """

  init c1 phase Phases.configObjects """
  U32 x = 0;
  """

  init c1 phase Phases.instances """
  C1 c1();
  """

  init c1 phase Phases.initComponents """
  c1.init();
  """

  init c1 phase Phases.configComponents """
  c1.config();
  """

  topology Basic {

    instance c1
    instance c2

    connections C {

      c2.p -> c1.p

    }

  }

}
