package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state or choice */
sealed trait StateOrChoice {
  def getSymbol: StateMachineSymbol
  def getName: String
}

object StateOrChoice {

  final case class State(symbol: StateMachineSymbol.State)
    extends StateOrChoice {
      def getSymbol = symbol
      def getName = s"state ${symbol.getUnqualifiedName}"
    }

  final case class Choice(symbol: StateMachineSymbol.Choice)
    extends StateOrChoice {
      def getSymbol = symbol
      def getName = s"choice ${symbol.getUnqualifiedName}"
    }

}
