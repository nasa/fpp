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

  /** Visit a list in sequence, threading state */
  def visitList[T](
    s: State,
    list: List[T],
    visit: (State, T) => Result
  ): Result = 
    Result.foldLeft (list) (s) (visit)
  
}
