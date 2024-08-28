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
      val msg = sma.parentSymbol match {
        case Some(symbol) =>
          s"initial transition of state must go to state or junction defined in the same state"
        case None =>
          s"initial transition of state machine may not go to a state or junction defined in a substate"
      }
      Left(SemanticError.StateMachine.InvalidInitialTransition(loc, msg))
    }
  }

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val loc = Locations.get(aNode._2.id)
    val members = aNode._2.data.members.map(_.node._2)
    val subStates = members.collect {
      case _: Ast.StateMember.DefState => ()
    }
    val initialTransitions = members.collect {
      case _: Ast.StateMember.SpecInitialTransition => ()
    }
    (subStates, initialTransitions) match {
      // No substates, no initial transition: OK
      case (Nil, Nil) => Right(sma)
      // No substates, initial transition: Error
      case (Nil, _) => Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          "state with no substates may not have an initial transition"
        )
      )
      // Substates: Check semantics
      case _ => for {
        // Check for exactly one initial transition
        _ <- checkOneInitialTransition(
          initialTransitions,
          loc,
          "state with substates"
        )
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
          s"$defKind must have initial transition"
        )
      )
      case head :: Nil => Right(())
      case _ => Left(
        SemanticError.StateMachine.InvalidInitialTransition(
          loc,
          s"$defKind has ${transitions.size} initial transitions; only one is allowed"
        )
      )
    }

}
