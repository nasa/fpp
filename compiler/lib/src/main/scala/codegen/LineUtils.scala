package fpp.compiler.codegen

/** Utilities for writing lines */
trait LineUtils {

  def indentIn(line: Line) = line.indentIn(indentIncrement)

  def line(s: String) = Line(s)

  def lines(s: String) = s.stripMargin.split("\n").map(line(_)).toList

  def linesOpt[T](f: T => List[Line], o: Option[T]) =
    o match {
      case Some(x) => f(x)
      case None => Nil
    }

  val q = "\""

  val indentIncrement = 2

  val addBlankPrefix = Line.addPrefixLine (Line.blank) _

  val addBlankPostfix = Line.addPostfixLine (Line.blank) _

  val flattenWithBlankPrefix = Line.flattenWithPrefixLine (Line.blank) _

}
