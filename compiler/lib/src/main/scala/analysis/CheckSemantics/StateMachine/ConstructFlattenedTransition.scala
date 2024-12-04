package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct a flattened transition */
case class ConstructFlattenedTransition(
  sma: StateMachineAnalysis,
  source: StateOrChoice
) {

  /** The interface method */
  def transition(transition: Transition): Transition =
    transition match {
      case external: Transition.External => externalTransition(external)
      case _: Transition.Internal => transition
    }

  // Flatten an external transition
  private def externalTransition(transition: Transition.External):
  Transition.External = {
    val target = transition.target
    val sourceStates = getSourceParentStateList(source)
    val targetStates = getTargetParentStateList(target)
    val (exitStates, entryStates) =
      deleteLongestCommonPrefix(sourceStates, targetStates)
    val actions = List.concat(
      exitStates.reverse.flatMap(getExitActions),
      transition.actions,
      entryStates.flatMap(getEntryActions)
    )
    Transition.External(actions, target)
  }

  // Get the parent state list of a source state or choice
  private def getSourceParentStateList(soc: StateOrChoice):
  List[StateMachineSymbol.State] = {
    val start = soc match {
      case StateOrChoice.State(state) => List(state)
      case StateOrChoice.Choice(_) => Nil
    }
    sma.getParentStateList(soc.getSymbol, start)
  }

  // Get the parent state list of a target state or choice
  private def getTargetParentStateList(soc: StateOrChoice):
  List[StateMachineSymbol.State] = sma.getParentStateList(soc.getSymbol)

  // Delete the longest common prefix of two lists
  private def deleteLongestCommonPrefix[T](
    list1: List[T],
    list2: List[T]
  ): (List[T], List[T]) = (list1, list2) match {
    case (head1 :: tail1, head2 :: tail2) =>
      if head1 == head2
      then deleteLongestCommonPrefix(tail1, tail2)
      else (list1, list2)
    case _ => (list1, list2)
  }

  // Get the entry actions of a state symbol
  private def getEntryActions(s: StateMachineSymbol.State):
  List[StateMachineSymbol.Action] =
    s.node._2.data.members.flatMap(
      _.node._2 match {
        case Ast.StateMember.SpecStateEntry(node) =>
          node.data.actions.map(sma.getActionSymbol)
        case _ => Nil
      }
    )

  // Get the exit actions of a state symbol
  private def getExitActions(s: StateMachineSymbol.State):
  List[StateMachineSymbol.Action] =
    s.node._2.data.members.flatMap(
      _.node._2 match {
        case Ast.StateMember.SpecStateExit(node) =>
          node.data.actions.map(sma.getActionSymbol)
        case _ => Nil
      }
    )

}
