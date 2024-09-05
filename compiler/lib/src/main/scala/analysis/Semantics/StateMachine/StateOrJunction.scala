package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state or junction */
sealed trait StateOrJunction {
  def getSymbol: StateMachineSymbol
  def getName: String
}

object StateOrJunction {

  final case class State(symbol: StateMachineSymbol.State)
    extends StateOrJunction {
      def getSymbol = symbol
      def getName = s"state ${symbol.getUnqualifiedName}"
    }

  final case class Junction(symbol: StateMachineSymbol.Junction)
    extends StateOrJunction {
      def getSymbol = symbol
      def getName = s"junction ${symbol.getUnqualifiedName}"
    }

}
