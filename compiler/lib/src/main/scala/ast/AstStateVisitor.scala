package fpp.compiler.ast

import fpp.compiler.util._

/** Visit an AST, carrying state */
trait AstStateVisitor extends AstVisitor {

  type State

  type In = State

  type Out = Result.Result[State]

  type Result = Result.Result[State]

  /** Default state transformation */
  override def default(s: State) = Right(s)

  /** Visit the members of an expanded template */
  override def specTemplateExpandAnnotatedNodeExpanded(
    s: State,
    node: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
    members: List[Ast.ModuleMember]
  ) = visitList(s, members, matchModuleMember)

  /** Visit a list in sequence, threading state */
  def visitList[T](
    s: State,
    list: List[T],
    visit: (State, T) => Result
  ): Result = 
    Result.foldLeft (list) (s) (visit)
  
}
