package fpp.compiler.util

/** The result of a compilation step */
object Result {

  type Result[T] = Either[Error, T]
  
  /** Left fold with a function that returns a result */
  def foldLeft[A,B]
    (as: List[A])
    (b: B)
    (f: (B, A) => Result.Result[B]): Result.Result[B] =
    as match {
      case Nil => Right(b)
      case a :: tail =>
        f(b, a) match {
          case Right(b) => foldLeft(tail)(b)(f)
          case Left(e) => Left(e)
        }
    }

  /** Apply a result function to each element of a list */
  def map[A,B](
    list: List[A], 
    f: A => Result[B]
  ): Result[List[B]] = {
    def helper(in: List[A], out: List[B]): Result[List[B]] = {
      in match {
        case Nil => Right(out.reverse)
        case head :: tail => f(head) match {
          case Left(e) => Left(e)
          case Right(b) => helper(tail, b :: out)
        }
      }
    }
    helper(list, Nil)
  }

  /** Applies a result function inside a Result */
  def mapResult[A,B](r: Result[A], f: A => Result[B]): Result[B] =
    r match {
      case Left(left) => Left(left)
      case Right(right) => f(right)
    }

  /** Applies a result function inside an option */
  def mapOpt[A,B](o: Option[A], f: A => Result[B]): Result[Option[B]] =
    o match {
      case Some(a) => f(a) match {
        case Right(b) => Right(Some(b))
        case Left(e) => Left(e)
      }
      case None => Right(None)
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
