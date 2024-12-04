package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A collection of name-symbol maps, one for each name group */
type StateMachineScope = GenericScope[StateMachineNameGroup,StateMachineSymbol]

object StateMachineScope {

  /** Create an empty Scope */
  def empty: StateMachineScope = GenericScope[StateMachineNameGroup,StateMachineSymbol]()

}

