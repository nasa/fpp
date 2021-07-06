module M {

  port P

  active component Active {

    async input port p: P

  }

  passive component Passive {

    output port p: P

  }

  instance active1: Active base id 0x100 \
    queue size 10 stack size 1024 priority 1
  instance active2: Active base id 0x200 \
    queue size 10 stack size 1024 priority 1
  instance passive1: Passive base id 0x300
  instance passive2: Passive base id 0x400

  init active2 phase Phases.configConstants """
  enum {
    X = 0,
    Y = 1
  };
  """

  init active2 phase Phases.configObjects """
  U32 x = 0;
  """

  init active2 phase Phases.instances """
  Active active1();
  """

  init active2 phase Phases.initComponents """
  active2.initSpecial();
  """

  init active2 phase Phases.configComponents """
  active2.config();
  """

  init active2 phase Phases.startTasks """
  active2.startSpecial();
  """

  init active2 phase Phases.stopTasks """
  active2.stopSpecial();
  """

  init active2 phase Phases.freeThreads """
  active2.freeSpecial();
  """

  init active2 phase Phases.tearDownComponents """
  active2.tearDown();
  """

  topology Basic {

    instance active1
    instance active2
    instance passive1
    instance passive2

    connections C1 {

      passive1.p -> active1.p

    }

    connections C2 {

      passive2.p -> active2.p

    }

  }

}
