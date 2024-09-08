package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP type option */
object TypeOption {

  type T = Option[Type]

  /** Shows a type option as a string */
  def show(to: T) = to.map(_.toString).getOrElse("None")

  /** Computes the common type option of two type options */
  def commonType(to1: T, to2: T): Option[T] =
    (to1, to2) match {
      case (Some(t1), Some(t2)) =>
        for { t <- Type.commonType(t1, t2) } yield Some(t)
      case _ => Some(None)
    }

  /** Converts to1 to to2 */
  def isConvertibleTo(to1: T, to2: T): Boolean = (to1, to2) match {
    case (Some(t1), Some(t2)) => t1.isConvertibleTo(t2)
    case (_, None) => true
    case (None, _) => false
  }

}
