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
      val initialTransitions = members.map(_.node._2).collect {
        case _: Ast.StateMachineMember.SpecInitialTransition => ()
      }
      checkOneInitialTransition(
        initialTransitions,
        Locations.get(aNode._2.id),
        "state machine"
      )
    }
    // Visit the members
    _ <- super.defStateMachineAnnotatedNodeInternal(
      sma.copy(parentSymbol = None),
      aNode,
      members
    )
  } yield sma

  override def specInitialTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) = {
    // Check that the state machine or junction referred to in aNode
    // has the same parent symbol as sma
    val destId = aNode._2.data.transition.destination.id
    val destSymbol = sma.useDefMap(destId)
    val destParentSymbol = sma.parentSymbolMap.get(destSymbol)
    if destParentSymbol == sma.parentSymbol
    then Right(sma)
    else {
      val loc = Locations.get(aNode._2.id)
      val context = sma.parentSymbol match {
        case Some(symbol) => (
          s"in state ${symbol.getUnqualifiedName}",
          "in same state"
        )
        case None => (
          s"at top level of state machine",
          "at top level"
        )
      }
      Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"initial transition ${context._1} must go to state or junction defined ${context._2}",
        )
      )
    }
  }

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    // Get the substates
    val subStates = aNode._2.data.members.map(_.node._2).collect {
      case _: Ast.StateMember.DefState => ()
    }
    subStates match {
      // Leaf state: nothing to do
      case Nil => Right(sma)
      // Inner state: check semantics
      case _ => for {
        _ <- {
          // Check that there is exactly one initial transition specifier
          val initialTransitions =
            aNode._2.data.members.map(_.node._2).collect {
              case _: Ast.StateMember.SpecInitialTransition => ()
            }
          checkOneInitialTransition(
            initialTransitions,
            Locations.get(aNode._2.id),
            "state"
          )
        }
        // Visit the members
        _ <- super.defStateAnnotatedNode(
          sma.copy(parentSymbol = Some(StateMachineSymbol.State(aNode))),
          aNode
        )
      }
      yield sma
    }
  }

  // Checks that there is exactly one initial transition specifier
  private def checkOneInitialTransition[T](
    transitions: List[T],
    loc: Location,
    defKind: String
  ): Result.Result[Unit] =
    transitions match {
      case Nil => Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"$defKind definition must have at least one initial transition"
        )
      )
      case head :: Nil => Right(())
      case _ => Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"$defKind definition has ${transitions.size} initial transitions; only one is allowed"
        )
      )
    }

}
