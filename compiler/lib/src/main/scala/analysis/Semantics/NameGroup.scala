package fpp.compiler.analysis

/** A name group */
sealed trait NameGroup 

object NameGroup {
  case object Component extends NameGroup
  case object Port extends NameGroup
  case object StateMachine extends NameGroup
  case object Instance extends NameGroup
  case object Interface extends NameGroup
  case object Type extends NameGroup
  case object Value extends NameGroup

  val groups: List[NameGroup] = List(
    Component,
    Port,
    StateMachine,
    Instance,
    Interface,
    Type,
    Value
  )

}
