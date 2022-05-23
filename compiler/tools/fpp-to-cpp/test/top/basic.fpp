module M {

  port P

  active component Active {

    async input port p: P

  }

  passive component Passive {

    output port p: P

  }

  instance active1: Active base id 0x100 \
    at "Active.hpp" \
    queue size 10 stack size 1024 priority 1 cpu 0
  instance active2: Active base id 0x200 \
    queue size 10 \
  {

    phase Phases.configConstants """
    enum {
      X = 0,
      Y = 1
    };
    """

    phase Phases.configObjects """
    U32 x = 0;
    """

    phase Phases.instances """
    Active active2;
    """

    phase Phases.initComponents """
    active2.initSpecial();
    """

    phase Phases.configComponents """
    active2.config();
    """

    phase Phases.startTasks """
    active2.startSpecial();
    """

    phase Phases.stopTasks """
    active2.stopSpecial();
    """

    phase Phases.freeThreads """
    active2.freeSpecial();
    """

    phase Phases.tearDownComponents """
    active2.tearDown();
    """

  }
  instance active3: Active base id 0x300 \
    at "Active.hpp" \
    queue size 10

  instance passive1: Passive base id 0x300
  instance passive2: Passive base id 0x400 type "ConcretePassive"

  topology Basic {

    instance active1
    instance active2
    instance active3
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
