package fpp.compiler.analysis

/** A state machine name group */
sealed trait StateMachineNameGroup

object StateMachineNameGroup {
  case object Action extends StateMachineNameGroup {
    override def toString(): String = "action"
  }

  case object Guard extends StateMachineNameGroup {
    override def toString(): String = "guard"
  }

  case object Signal extends StateMachineNameGroup {
    override def toString(): String = "signal"
  }

  case object State extends StateMachineNameGroup {
    override def toString(): String = "state"
  }

  val groups: List[StateMachineNameGroup] = List(
    Action,
    Guard,
    Signal,
    State,
  )

}
