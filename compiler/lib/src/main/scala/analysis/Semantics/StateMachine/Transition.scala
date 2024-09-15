package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine transition */
sealed trait Transition {
  def getActions: List[StateMachineSymbol.Action]
}

object Transition {

  /** An external transition */
  final case class External(
    actions: List[StateMachineSymbol.Action],
    target: StateOrJunction
  ) extends Transition {
    def getActions = actions
  }

  /** An internal transition */
  final case class Internal(actions: List[StateMachineSymbol.Action])
    extends Transition {
      def getActions = actions
    }

  /** A guarded transition */
  case class  Guarded(
    guardOpt: Option[StateMachineSymbol.Guard],
    transition: Transition
  )

}
