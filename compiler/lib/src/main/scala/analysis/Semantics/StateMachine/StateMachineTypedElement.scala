package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A typed element of a state machine */
sealed trait StateMachineTypedElement {
  def getNodeId: AstNode.Id
}

object StateMachineTypedElement {

  final case class InitialTransition(
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
  }

  final case class StateTransition(
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
  }

  final case class Junction(
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) extends StateMachineTypedElement {
    def getNodeId = aNode._2.id
  }

}
