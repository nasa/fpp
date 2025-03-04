module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
  port PrmGet
  port PrmSet
  port Time
  port Tlm
}

enum Phases {
  configConstants
  configObjects
  instances
  initComponents
  configComponents
  regCommands
  readParameters
  loadParameters
  startTasks
  stopTasks
  freeThreads
  tearDownComponents
}
