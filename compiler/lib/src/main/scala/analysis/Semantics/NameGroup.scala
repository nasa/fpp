package fpp.compiler.analysis

/** A name group */
sealed trait NameGroup 

object NameGroup {
  case object Component extends NameGroup
  case object Port extends NameGroup
  case object StateMachine extends NameGroup
  case object PortInterfaceInstance extends NameGroup
  case object PortInterface extends NameGroup
  case object Type extends NameGroup
  case object Value extends NameGroup

  val groups: List[NameGroup] = List(
    Component,
    Port,
    StateMachine,
    PortInterfaceInstance,
    PortInterface,
    Type,
    Value
  )

}
