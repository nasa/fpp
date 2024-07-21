package fpp.compiler.analysis

/** A state machine name group */
sealed trait StateMachineNameGroup

object StateMachineNameGroup {
  case object Action extends StateMachineNameGroup
  case object Guard extends StateMachineNameGroup
  case object Signal extends StateMachineNameGroup
  case object State extends StateMachineNameGroup

  val groups: List[StateMachineNameGroup] = List(
    Action,
    Guard,
    Signal,
    State,
  )

}
