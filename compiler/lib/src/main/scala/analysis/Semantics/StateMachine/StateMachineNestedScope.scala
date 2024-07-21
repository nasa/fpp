package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A stack of scopes */
type StateMachineNestedScope = GenericNestedScope[StateMachineNameGroup, StateMachineSymbol]

object StateMachineNestedScope {

  /** Create an empty NestedScope */
  def empty: StateMachineNestedScope = GenericNestedScope(List(StateMachineScope.empty))

}
