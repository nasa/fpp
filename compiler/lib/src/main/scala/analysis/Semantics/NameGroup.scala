package fpp.compiler.analysis

/** A name group */
sealed trait NameGroup 

object NameGroup {
  case object ComponentInstance extends NameGroup
  case object Component extends NameGroup
  case object Port extends NameGroup
  case object Topoology extends NameGroup
  case object Type extends NameGroup
  case object Value extends NameGroup
}
