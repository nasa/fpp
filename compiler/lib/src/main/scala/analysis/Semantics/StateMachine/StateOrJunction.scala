package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state or junction */
sealed trait StateOrJunction

object StateOrJunction {

  final case class State(symbol: StateMachineSymbol.State)
    extends StateOrJunction

  final case class Junction(symbol: StateMachineSymbol.Junction)
    extends StateOrJunction

}
