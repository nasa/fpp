package fpp.compiler.ast

import fpp.compiler.util._

/** Visit an AST, carrying state */
trait AstStateVisitor extends AstVisitor {

  type State

  type In = State

  type Out = Result.Result[State]

  type Result = Result.Result[State]

  /** Visit a list in sequence, threading state */
  def visitList[T](
    s: State,
    list: List[T],
    visit: (State, T) => Result
  ): Result = {
    list match {
      case Nil => Right(s)
      case head :: tail => visit(s, head) match {
        case Left(e) => Left(e)
        case Right(s) => visitList(s, tail, visit)
      }
    }

  }

}
