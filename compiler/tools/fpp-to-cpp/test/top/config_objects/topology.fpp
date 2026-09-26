instance first: M.C base id 256 {
  phase Phases.configObjects "U32 value = 11;"
  phase Phases.initComponents """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.configComponents """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.regCommands """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.readParameters """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.loadParameters """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.startTasks """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.stopTasks """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.freeThreads """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.tearDownComponents """
  const U32 local = value;
  first.record(local);
  """
  phase Phases.deinitComponents """
  const U32 local = value;
  first.record(local);
  """
}

module M {
  module Nested {
instance second: M.C base id 512 {
  phase Phases.configObjects "U32 value = 22;"
  phase Phases.initComponents """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.configComponents """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.regCommands """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.readParameters """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.loadParameters """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.startTasks """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.stopTasks """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.freeThreads """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.tearDownComponents """
  const U32 local = value;
  M::Nested::second.record(local);
  """
  phase Phases.deinitComponents """
  const U32 local = value;
  M::Nested::second.record(local);
  """
}
  }
}

instance noConfig: M.C base id 0x300 {
  phase Phases.configComponents "noConfig.record(33);"
}

instance emptyConfig: M.C base id 0x400 {
  phase Phases.configObjects ""
  phase Phases.configComponents "emptyConfig.record(44);"
}

instance emptyPhase: M.C base id 0x500 {
  phase Phases.configObjects "U32 value = 55;"
  phase Phases.configComponents ""
}

module M {
  deployment topology ConfigObjects {
    instance first
    instance Nested.second
    instance noConfig
    instance emptyConfig
    instance emptyPhase
  }
}
