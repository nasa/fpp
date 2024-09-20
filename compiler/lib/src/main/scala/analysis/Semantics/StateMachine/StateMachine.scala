package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine */
case class StateMachine(
  /** The AST node defining the state machine */
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
  /** The state machine analysis */
  sma: StateMachineAnalysis
)

object StateMachine {

  sealed trait Kind
  object Kind {
    case object External extends Kind
    case object Internal extends Kind
  }

  def getSubstates(state: Ast.DefState): List[Ast.Annotated[AstNode[Ast.DefState]]] =
    state.members.collect {
      case Ast.StateMember((pre, Ast.StateMember.DefState(node), post)) => (pre, node, post)
    }

  def getLeafStates(sym: Symbol.StateMachine): Set[Ast.Annotated[AstNode[Ast.DefState]]] =
    LeafStateVisitor.defStateMachineAnnotatedNode(Set(), sym.node)

  private object LeafStateVisitor extends AstVisitor {

    type States = Set[Ast.Annotated[AstNode[Ast.DefState]]]
    type In = States
    type Out = States

    def default(states: States) = states

    override def defStateMachineAnnotatedNodeInternal(
      states: States,
      aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
      members: List[Ast.StateMachineMember]
    ): States = members.foldLeft (states) (matchStateMachineMember)

    override def defStateAnnotatedNode(
      states: States,
      aNode: Ast.Annotated[AstNode[Ast.DefState]]
    ): States = {
      val data = aNode._2.data
      getSubstates(data) match {
        case Nil => states + aNode
        case _ => data.members.foldLeft (states) (matchStateMember)
      }
    }

  }

  def getSymbolKind(sym: Symbol.StateMachine): Kind =
    sym.node._2.data.members match {
      case None => Kind.External
      case _ => Kind.Internal
    }

}
