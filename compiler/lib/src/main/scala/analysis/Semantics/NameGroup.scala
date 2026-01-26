package fpp.compiler.analysis

/** A name group */
sealed trait NameGroup 

object NameGroup {
  case object ComponentInstance extends NameGroup {
    override def toString(): String = "component instance"
  }

  case object Component extends NameGroup {
    override def toString(): String = "component"
  }

  case object Port extends NameGroup {
    override def toString(): String = "port"
  }

  case object StateMachine extends NameGroup {
    override def toString(): String = "state machine"
  }

  case object Topology extends NameGroup {
    override def toString(): String = "topology"
  }

  case object Interface extends NameGroup {
    override def toString(): String = "interface"
  }

  case object Type extends NameGroup {
    override def toString(): String = "type"
  }

  case object Value extends NameGroup {
    override def toString(): String = "constant"
  }

  val groups: List[NameGroup] = List(
    ComponentInstance,
    Component,
    Port,
    StateMachine,
    Topology,
    Interface,
    Type,
    Value
  )

}
