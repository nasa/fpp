package fpp.compiler.util

/** The result of a compilation step */
object Result {

  type Result[T] = Either[Error, T]
  
  /** Apply a result function to each element of a list */
  def map[A,B](
    list: List[A], 
    f: A => Result[B]
  ): Result[List[B]] = {
    def mapHelper(
      list: List[A],
      results: List[B]
    ): Result[List[B]] = {
      list match {
        case head :: tail => {
          val result = f(head)
          result match {
            case Left(e) => Left(e)
            case Right(tu) => mapHelper(tail, tu :: results)
          }
        }
        case Nil => Right(results)
      }
    }
    mapHelper(list, Nil)
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

}
