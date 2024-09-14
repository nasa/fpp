package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine transition */
sealed trait Transition {
  def getActions: List[StateMachineSymbol.Action]
}

object Transition {

  final case class External(
    actions: List[StateMachineSymbol.Action],
    target: StateOrJunction
  ) extends Transition {
    def getActions = actions
  }

  final case class Internal(actions: List[StateMachineSymbol.Action])
    extends Transition {
      def getActions = actions
    }

}
