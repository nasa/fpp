package fpp.compiler.util

/** The result of a compilation step */
object Result {

  type Result[T] = Either[Error, T]
  
  /** Apply a result function to each element of a list */
  def map[A,B](
    list: List[A], 
    f: A => Result[B]
  ): Result[List[B]] = {
    def helper(in: List[A], out: List[B]): Result[List[B]] = {
      in match {
        case Nil => Right(out)
        case head :: tail => f(head) match {
          case Left(e) => Left(e)
          case Right(b) => helper(tail, b :: out)
        }
      }
    }
    helper(list, Nil)
  }

  /** Apply a list of result functions in sequence */
  def seq[A](
    r: Result[A],
    fs: List[A => Result[A]]
  ): Result[A] = {
    (r, fs) match {
      case (Right(a), f :: fs1) => seq(f(a), fs1)
      case _ => r
    }
  }

  /** Expect a Right result; throw InternalException otherwise */
  def expectRight[T](result: Result[T]): T = result match {
    case Right(value) => value
    case Left(e) => throw new InternalError(s"unexpected error ${e}")
  }

}
