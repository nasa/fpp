package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP annotation as C++ */
object AnnotationCppWriter extends LineUtils {

  /** Writes a pre comment */
  def writePreComment[T](a: Ast.Annotated[T]): List[Line] =
    (a._1 ++ a._3).map(s => "//! " ++ s).map(line)

  /** Writes a post comment */
  def writePostComment[T](a: Ast.Annotated[T]): List[Line] =
    (a._1 ++ a._3).map(s => "//!< " ++ s).map(line)

  /** Converts an annotation to a newline-separated string */
  def asStringOpt[T](a: Ast.Annotated[T]): Option[String] =
    (a._1 ++ a._3).map(line) match {
      case Nil => None
      case ll => Some((Line.flatten ("\n") (ll)).toString)
    }

}
