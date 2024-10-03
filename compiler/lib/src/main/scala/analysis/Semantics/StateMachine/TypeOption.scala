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
        if Type.areIdentical(t1, t2)
        then Some(Some(t1))
        else (t1, t2) match {
          case (int1 : Type.PrimitiveInt, int2 : Type.PrimitiveInt) =>
            if int1.signedness != int2.signedness
            then None
            else if int2.bitWidth > int1.bitWidth
                 then Some(to2)
                 else Some(to1)
          case (float1 : Type.Float, float2 : Type.Float) =>
            if float2.bitWidth > float1.bitWidth
            then Some(to2)
            else Some(to1)
          case (Type.String(_), Type.String(_)) =>
            Some(Some(Type.String(None)))
          case _ => None
        }
      case _ => Some(None)
    }

  /** Checks whether to1 is convertible to to2 */
  def isConvertibleTo(to1: T, to2: T): Boolean = (to1, to2) match {
    case (Some(t1), Some(t2)) =>
      if Type.areIdentical(t1, t2)
        then true
        else (t1, t2) match {
          case (int1 : Type.PrimitiveInt, int2 : Type.PrimitiveInt) =>
            (int1.signedness == int2.signedness) &&
            (int2.bitWidth >= int1.bitWidth)
          case (float1 : Type.Float, float2 : Type.Float) =>
            (float2.bitWidth >= float1.bitWidth)
          case (Type.String(_), Type.String(_)) => true
          case _ => false
        }
    case (_, None) => true
    case (None, _) => false
  }

}
