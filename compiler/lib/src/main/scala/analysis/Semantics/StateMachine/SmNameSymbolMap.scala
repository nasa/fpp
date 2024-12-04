package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._


/** A local mapping of unqualified names to state machine symbols */
type SmNameSymbolMap = GenericNameSymbolMap[StateMachineSymbol]

object SmNameSymbolMap {

  val empty = GenericNameSymbolMap[StateMachineSymbol]()

}

