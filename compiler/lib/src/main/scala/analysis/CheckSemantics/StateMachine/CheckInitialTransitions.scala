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
    checkForDestOutsideParent(
      sma,
      destSymbol,
      sma.parentSymbol
    ) match {
      case Right(_) => Right(sma)
      case Left(defSymbol) =>
        val loc = Locations.get(aNode._2.id)
        val defLoc = Locations.get(defSymbol.getNodeId)
        val msg = sma.parentSymbol match {
          case Some(symbol) =>
            s"initial transition of state must go to state or junction defined in the same state"
          case None =>
            s"initial transition of state machine may not go to a state or junction defined in a substate"
        }
        Left(SemanticError.StateMachine.InvalidInitialTransition(loc, msg, Some(defLoc)))
    }
  }

  // Checks for a transition destination outside the parent
  // Returns the symbol (error) or the set of visited symbols
  private def checkForDestOutsideParent(
    sma: StateMachineAnalysis,
    destSymbol: StateMachineSymbol,
    parentSymbol: Option[StateMachineSymbol],
    visitedSymbols: Set[StateMachineSymbol] = Set()
  ): Either[StateMachineSymbol, Set[StateMachineSymbol]] =
    if (visitedSymbols.contains(destSymbol))
    // We have visited this symbol already: nothing to do
    then Right(visitedSymbols)
    // We haven't visited the symbol
    else {
      val destParentSymbol = sma.parentSymbolMap.get(destSymbol)
      for {
        // Check that the symbol is defined in the parent
        visitedSymbols <- if (destParentSymbol == parentSymbol)
                          then Right(visitedSymbols + destSymbol)
                          else Left(destSymbol)
        // Recursively check junction destinations
        visitedSymbols <- destSymbol match {
          // Junction
          case StateMachineSymbol.Junction(aNode) => 
            for {
              // Recursively check if transition
              visitedSymbols <- {
                val ifTransitionId = aNode._2.data.ifTransition.id
                val ifTransitionSymbol = sma.useDefMap(ifTransitionId)
                checkForDestOutsideParent(
                  sma,
                  ifTransitionSymbol,
                  parentSymbol,
                  visitedSymbols
                )
              }
              // Recursively check else transition
              visitedSymbols <- {
                val elseTransitionId = aNode._2.data.elseTransition.id
                val elseTransitionSymbol = sma.useDefMap(elseTransitionId)
                checkForDestOutsideParent(
                  sma,
                  elseTransitionSymbol,
                  parentSymbol,
                  visitedSymbols
                )
              }
            } yield visitedSymbols
          // Not a junction: nothing to do
          case _ => Right(visitedSymbols)
        }
      }
      yield visitedSymbols
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
