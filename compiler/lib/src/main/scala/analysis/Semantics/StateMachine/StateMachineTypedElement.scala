package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A typed element of a state machine */
sealed trait StateMachineTypedElement {
  def getNodeId: AstNode.Id
  def showKind: String
}

object StateMachineTypedElement {

  final case class StateEntry(
    aNode: Ast.Annotated[AstNode[Ast.SpecStateEntry]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
    def showKind = "entry actions"
  }

  final case class StateExit(
    aNode: Ast.Annotated[AstNode[Ast.SpecStateExit]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
    def showKind = "exit actions"
  }

  final case class InitialTransition(
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
    def showKind = "initial transition"
  }

  final case class StateTransition(
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
    def showKind = "state transition"
  }

  final case class Choice(
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
    def showKind = "choice"
  }

}
