package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine */
case class StateMachine(
  /** The AST node defining the state machine */
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
  /** The state machine analysis */
  sma: StateMachineAnalysis
) {

  val actions = StateMachine.getActions(aNode._2.data).map(StateMachineSymbol.Action(_))

  val hasActions = !actions.isEmpty

  val guards = StateMachine.getGuards(aNode._2.data).map(StateMachineSymbol.Guard(_))

  val hasGuards = !guards.isEmpty

  val signals = StateMachine.getSignals(aNode._2.data).map(StateMachineSymbol.Signal(_))

  val hasSignals = !signals.isEmpty

  def getSymbol: Symbol.StateMachine = Symbol.StateMachine(aNode)

  def getKind: StateMachine.Kind = StateMachine.getSymbolKind(getSymbol)

}

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

  def getInitialSpecifier(sm: Ast.DefStateMachine):
  Ast.Annotated[AstNode[Ast.SpecInitialTransition]] = {
    val specifiers = sm.members.getOrElse(Nil).collect {
      case Ast.StateMachineMember(
        (pre, Ast.StateMachineMember.SpecInitialTransition(node), post)
      ) => (pre, node, post)
    }
    specifiers match {
      case head :: Nil => head
      case _ => throw new InternalError(
        "state machine must have exactly one initial transition specifier"
      )
    }
  }

  def getActions(stateMachine: Ast.DefStateMachine):
  List[Ast.Annotated[AstNode[Ast.DefAction]]] =
    stateMachine.members.getOrElse(Nil).collect {
      case Ast.StateMachineMember(
        (pre, Ast.StateMachineMember.DefAction(node), post)
      ) => (pre, node, post)
    }

  def getGuards(stateMachine: Ast.DefStateMachine):
  List[Ast.Annotated[AstNode[Ast.DefGuard]]] =
    stateMachine.members.getOrElse(Nil).collect {
      case Ast.StateMachineMember(
        (pre, Ast.StateMachineMember.DefGuard(node), post)
      ) => (pre, node, post)
    }

  def getSignals(stateMachine: Ast.DefStateMachine):
  List[Ast.Annotated[AstNode[Ast.DefSignal]]] =
    stateMachine.members.getOrElse(Nil).collect {
      case Ast.StateMachineMember(
        (pre, Ast.StateMachineMember.DefSignal(node), post)
      ) => (pre, node, post)
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
      State.getSubstates(data) match {
        case Nil => states + aNode
        case _ => data.members.foldLeft (states) (matchStateMember)
      }
    }

  }

}
