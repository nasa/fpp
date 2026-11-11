package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Transform an AST, producing a unit Out value */
trait AstUnitTransformer extends AstTransformer
{

  type Out = Unit

  /** Transform a list in sequence */
  def transformList[A, B](
    in: In,
    list: List[A],
    transform: (In, A) => Result[B]
  ): Result.Result[List[B]] = {
    def helper(inList: List[A], outList: List[B]): Result.Result[List[B]] = {
      inList match {
        case Nil => Right(outList)
        case head :: tail => transform(in, head) match {
          case Left(e) => Left(e)
          case Right((_, list)) => helper(tail, list :: outList)
        }
      }
    }
    helper(list, Nil).map(_.reverse)
  }

  /** Transform a list and flatten the result */
  def transformFlatList[A, B](
    in: In,
    list: List[A],
    transform: (In, A) => Result[List[B]]
  ): Result.Result[List[B]] = transformList(in, list, transform).map(_.flatten)

  /** Adapt a simple result function to the form of the visitor functions */
  def resultAdapter[A,B](f: (A, B) => Result.Result[B]) =
    (a: A, b: B) => for (c <- f(a, b)) yield ((), c)

}
