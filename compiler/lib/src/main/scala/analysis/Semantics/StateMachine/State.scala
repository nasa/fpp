package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A state of an FPP state machine */
object State {

  def getSubstates(state: Ast.DefState): List[Ast.Annotated[AstNode[Ast.DefState]]] =
    state.members.collect {
      case Ast.StateMember((pre, Ast.StateMember.DefState(node), post)) => (pre, node, post)
    }

  private def listToOpt[T](list: List[T], itemKind: String): Option[T] = list match {
    case Nil => None
    case head :: Nil => Some(head)
    case _ => throw new InternalError(s"state should have at most one $itemKind")
  }

  def getEntrySpecifierOpt(state: Ast.DefState): Option[Ast.Annotated[AstNode[Ast.SpecStateEntry]]] = {
    val specifiers = state.members.collect {
      case Ast.StateMember(
        (pre, Ast.StateMember.SpecStateEntry(node), post)
      ) => (pre, node, post)
    }
    listToOpt(specifiers, "entry specifier")
  }

  def getEntryActions(state: Ast.DefState): List[AstNode[Ast.Ident]] =
    getEntrySpecifierOpt(state).map(_._2.data.actions).getOrElse(Nil)

  def getExitSpecifierOpt(state: Ast.DefState): Option[Ast.Annotated[AstNode[Ast.SpecStateExit]]] = {
    val specifiers = state.members.collect {
      case Ast.StateMember(
        (pre, Ast.StateMember.SpecStateExit(node), post)
      ) => (pre, node, post)
    }
    listToOpt(specifiers, "exit specifier")
  }

  def getInitialSpecifier(state: Ast.DefState):
  Option[Ast.Annotated[AstNode[Ast.SpecInitialTransition]]] = {
    val specifiers = state.members.collect {
      case Ast.StateMember(
        (pre, Ast.StateMember.SpecInitialTransition(node), post)
      ) => (pre, node, post)
    }
    listToOpt(specifiers, "initial transition")
  }

  def getExitActions(state: Ast.DefState): List[AstNode[Ast.Ident]] =
    getExitSpecifierOpt(state).map(_._2.data.actions).getOrElse(Nil)

}
