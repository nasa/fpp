package fpp.compiler.util
import fpp.compiler.ast._

object Options {

  /** Apply a function returning Option to each element of a list.
   *  Stop and return None if any application returns None. */
  def map[A,B](
    list: List[A], 
    f: A => Option[B]
  ): Option[List[B]] = {
    def helper(in: List[A], out: List[B]): Option[List[B]] = {
      in match {
        case Nil => Some(out)
        case head :: tail => f(head) match {
          case Some(b) => helper(tail, b :: out)
          case None => None
        }
      }
    }
    helper(list.reverse, Nil)
  }

}
