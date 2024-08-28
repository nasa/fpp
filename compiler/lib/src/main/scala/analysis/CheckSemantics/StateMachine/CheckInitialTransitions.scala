package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check initial transitions */
object CheckInitialTransitions
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  override def defStateMachineAnnotatedNodeInternal(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = for {
    // Check that there is exactly one initial transition specifier
    _ <- {
      val n = members.count(
        m => m.node._2 match {
          case _: Ast.StateMachineMember.SpecInitialTransition => true
          case _ => false
        }
      )
      checkOneInitialTransition(n, Locations.get(aNode._2.id), "state machine")
    }
    // Visit the state machine members
    sma <- super.defStateMachineAnnotatedNodeInternal(
      sma.copy(parentSymbol = None),
      aNode,
      members
    )
  } yield sma

  override def specInitialTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]) = {
    // Check that the state machine or junction referred to in aNode
    // has the same parent symbol as sma
    val destId = aNode._2.data.transition.destination.id
    val destSymbol = sma.useDefMap(destId)
    val destParentSymbol = sma.parentSymbolMap.get(destSymbol)
    if destParentSymbol == sma.parentSymbol
    then Right(sma)
    else {
      val loc = Locations.get(aNode._2.id)
      val destDefLoc = Locations.get(destSymbol.getNodeId)
      val requiredDestDefLoc = sma.parentSymbol match {
        case Some(symbol) => s"in state ${symbol.getUnqualifiedName}"
        case None => "at top level of state machine"
      }
      Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"initial transition must go to state or junction $requiredDestDefLoc",
          Some(destDefLoc)
        )
      )
    }
  }

  override def defStateAnnotatedNodeInner(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]],
    states: List[Ast.Annotated[AstNode[Ast.DefState]]]
  ) = for {
    // Check that the state def has exactly one initial transition
    _ <- {
           val n = aNode._2.data.members.count(
             m => m.node._2 match {
               case _: Ast.StateMember.SpecInitialTransition => true
               case _ => false
             }
           )
           checkOneInitialTransition(n, Locations.get(aNode._2.id), "state")
         }
    // Visit the members
    sma <- super.defStateAnnotatedNode(
      sma.copy(parentSymbol = Some(StateMachineSymbol.State(aNode))),
      aNode
    )
  } yield sma

  // Checks that there is exactly one initial transition specifier
  private def checkOneInitialTransition(
    n: Int,
    loc: Location,
    defKind: String
  ): Result.Result[Unit] =
    n match {
      case 0 => Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"$defKind definition must have at least one initial transition"
        )
      )
      case 1 => Right(())
      case n => Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"$defKind definition has $n initial transitions; only one is allowed"
        )
      )
    }

}
