package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Utilities for writing lines */
trait LineUtils {

  def indentIn(line: Line) = line.indentIn(2)

  def line(s: String) = Line(s)

  def lines(s: String) = List(line(s))

  def linesOpt[T](f: T => List[Line], o: Option[T]) =
    o match {
      case Some(x) => f(x)
      case None => Nil
    }

}
