package fpp.compiler.ast

import fpp.compiler.util._

/** Transform an AST, carrying state */
trait AstStateTransformer extends AstTransformer {

  type State

  type In = State

  type Out = State

  override def transUnit(s: State, tu: Ast.TransUnit) = {
    for { result <- transformList(s, tu.members, tuMember) }
    yield (result._1, Ast.TransUnit(result._2.flatten))
  }

  /** Transform a list in sequence, threading state */
  def transformList[A,B](
    s: State,
    list: List[A],
    transform: (State, A) => Result[B]
  ): Result[List[B]] = {
    def helper(s: State, in: List[A], out: List[B]): Result[List[B]] = {
      in match {
        case Nil => Right((s, out))
        case head :: tail => transform(s, head) match {
          case Left(e) => Left(e)
          case Right((s, list)) => helper(s, tail, list :: out)
        }
      }
    }
    for { pair <- helper(s, list, Nil) }
    yield (pair._1, pair._2.reverse)
  }

  def tuMember(s: State, member: Ast.TUMember): Result[List[Ast.TUMember]] =
    matchModuleMember(s, member)

}
