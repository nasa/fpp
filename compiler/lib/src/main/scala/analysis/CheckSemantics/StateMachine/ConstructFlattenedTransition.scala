package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct a flattened transition */
case class ConstructFlattenedTransition(
  sma: StateMachineAnalysis,
  source: StateOrJunction
) {

  /** The interface method */
  def transition(transition: Transition): Transition =
    transition match {
      case external: Transition.External => externalTransition(external)
      case _: Transition.Internal => transition
    }

  // Flatten an external transition
  private def externalTransition(transition: Transition.External): Transition = {
    val target = transition.target
    val sourceStates = getParentStateList(source)
    val targetStates = getParentStateList(target)
    val prefix = {
      val reversedPrefix =
        getReversedCommonPrefix(sourceStates, targetStates)
      val adjustedReversedPrefix = adjustForSelfTransition(
        source,
        sourceStates,
        target,
        targetStates,
        reversedPrefix
      )
      adjustedReversedPrefix.reverse
    }
    val exitStates = removePrefix(prefix, sourceStates).reverse
    val entryStates = removePrefix(prefix, targetStates)
    val actions = List.concat(
      exitStates.flatMap(getExitActions),
      transition.actions,
      entryStates.flatMap(getEntryActions)
    )
    Transition.External(actions, target)
  }

  // Get the parent state list of a state or junction
  private def getParentStateList(soj: StateOrJunction):
  List[StateMachineSymbol.State] = soj match {
    case StateOrJunction.State(state) =>
      sma.getParentStateList(state) :+ state
    case StateOrJunction.Junction(junction) =>
      sma.getParentStateList(junction)
  }

  // Compute the reverse of the common prefix of two lists
  private def getReversedCommonPrefix[T](
    list1: List[T],
    list2: List[T]
  ): List[T] = {
    def helper(
      list1: List[T],
      list2: List[T],
      result: List[T]
    ): List[T] = (list1, list2) match {
      case (head1 :: tail1, head2 :: tail2) =>
        if head1 == head2
        then helper(tail1, tail2, head1 :: result)
        else result
      case _ => result
    }
    helper(list1, list2, Nil)
  }

  // Adjust the reversed common prefix of a list of state symbols
  // to account for a self transition
  private def adjustForSelfTransition(
    source: StateOrJunction,
    sourceStates: List[StateMachineSymbol.State],
    target: StateOrJunction,
    targetStates: List[StateMachineSymbol.State],
    reversedPrefix: List[StateMachineSymbol.State]
  ): List[StateMachineSymbol.State] = {
    val prefix = reversedPrefix.reverse
    (source, target, reversedPrefix) match {
      case (
        StateOrJunction.State(_),
        StateOrJunction.State(_),
        head :: tail
      ) =>
        if ((prefix == sourceStates) || (prefix == targetStates))
        then tail
        else reversedPrefix
      case _ => reversedPrefix
    }
  }

  // Remove a prefix of a list
  private def removePrefix[T](
    prefix: List[T],
    list: List[T]
  ): List[T] = {
    lazy val error = new InternalError("list does not have the specified prefix")
    (prefix, list) match {
      case (Nil, _) => list
      case (head1 :: tail1, head2 :: tail2) =>
        if head1 == head2
        then removePrefix(tail1, tail2)
        else throw error
      case _ => throw error
    }
  }

  // Get the entry actions of a state symbol
  private def getEntryActions(s: StateMachineSymbol.State):
  List[StateMachineSymbol.Action] =
    s.node._2.data.members.flatMap(
      _.node._2 match {
        case Ast.StateMember.SpecStateEntry(node) =>
          node.data.actions.map(getActionSymbol)
        case _ => Nil
      }
    )

  // Get the exit actions of a state symbol
  private def getExitActions(s: StateMachineSymbol.State):
  List[StateMachineSymbol.Action] =
    s.node._2.data.members.flatMap(
      _.node._2 match {
        case Ast.StateMember.SpecStateExit(node) =>
          node.data.actions.map(getActionSymbol)
        case _ => Nil
      }
    )

  // Get an action symbol from an identifier node
  private def getActionSymbol(action: AstNode[Ast.Ident]):
  StateMachineSymbol.Action = {
    val sym = sma.useDefMap(action.id)
    val actionSym @ StateMachineSymbol.Action(_) = sym
    actionSym
  }

}
