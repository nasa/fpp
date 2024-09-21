package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A state of an FPP state */
object State {

  def getSubstates(state: Ast.DefState): List[Ast.Annotated[AstNode[Ast.DefState]]] =
    state.members.collect {
      case Ast.StateMember((pre, Ast.StateMember.DefState(node), post)) => (pre, node, post)
    }

  def getEntrySpecifiers(state: Ast.DefState): List[Ast.Annotated[AstNode[Ast.SpecStateEntry]]] =
    state.members.collect {
      case Ast.StateMember((pre, Ast.StateMember.SpecStateEntry(node), post)) => (pre, node, post)
    }

  def getEntryActions(state: Ast.DefState): List[AstNode[Ast.Ident]] =
    getEntrySpecifiers(state).foldLeft (Nil: List[AstNode[Ast.Ident]]) (
      (actions, aNode) => actions ++ aNode._2.data.actions
    )

  def getExitSpecifiers(state: Ast.DefState): List[Ast.Annotated[AstNode[Ast.SpecStateExit]]] =
    state.members.collect {
      case Ast.StateMember((pre, Ast.StateMember.SpecStateExit(node), post)) => (pre, node, post)
    }

  def getExitActions(state: Ast.DefState): List[AstNode[Ast.Ident]] =
    getExitSpecifiers(state).foldLeft (Nil: List[AstNode[Ast.Ident]]) (
      (actions, aNode) => actions ++ aNode._2.data.actions
    )

}
