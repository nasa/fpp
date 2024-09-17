package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine */
case class StateMachine(
  /** The AST node defining the state machine */
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
  /** The state machine analysis */
  stateMachineAnalysis: StateMachineAnalysis
)

object StateMachine {

  sealed trait Kind
  object Kind {
    case object External extends Kind
    case object Internal extends Kind
  }

  def getSymbolKind(sym: Symbol.StateMachine): Kind =
    sym.node._2.data.members match {
      case None => Kind.External
      case _ => Kind.Internal
    }

}
