package fpp.compiler.analysis

/** A name group */
sealed trait NameGroup 

object NameGroup {
  case object Component extends NameGroup
  case object Port extends NameGroup
  case object StateMachine extends NameGroup
  case object PortInterfaceInstance extends NameGroup
  case object PortInterface extends NameGroup
  case object Template extends NameGroup
  case object Type extends NameGroup
  case object Value extends NameGroup

  // Template parameters can only be referred inside an expansion
  case object TemplateParam extends NameGroup

  val groups: List[NameGroup] = List(
    Component,
    Port,
    StateMachine,
    PortInterfaceInstance,
    PortInterface,
    Template,
    Type,
    Value
  )

}
